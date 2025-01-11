package org.rsmod.api.registry.obj

import jakarta.inject.Inject
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ObjRegistry
@Inject
constructor(private val updates: ZoneUpdateMap, private val objTypes: ObjTypeList) {
    public val objs: ZoneObjMap = ZoneObjMap()

    public fun count(): Int = objs.objCount()

    public fun add(obj: Obj): ObjRegistryResult {
        val stackable = obj.isStackable()
        if (!stackable) {
            check(obj.count < MAX_NON_STACK_COUNT_DROP) {
                "Cannot call `add` with this many non-stackable " +
                    "objs: obj=$obj, max=$MAX_NON_STACK_COUNT_DROP"
            }
        }

        val zoneKey = ZoneKey.from(obj.coords)
        val entryList = objs.getOrPut(zoneKey)
        val entries = entryList.findAll(obj.coords)
        val merge = entries.firstOrNull { it.canMergeWith(obj, stackable) }

        if (merge != null) {
            val oldCount = merge.count
            merge.change(merge.count + obj.count)
            updates.objCount(merge, merge.count, oldCount)
            return ObjRegistryResult.Merge(merge)
        }

        val splitCount = !stackable && obj.count > 1
        if (splitCount) {
            val split = ArrayList<Obj>(obj.count)
            repeat(obj.count) {
                val single = obj.singleCopy()
                split += single
                entryList.add(single)
                updates.objAdd(single)
            }
            return ObjRegistryResult.Split(split)
        }

        entryList.add(obj)
        updates.objAdd(obj)
        return ObjRegistryResult.Stack
    }

    public fun del(obj: Obj): Boolean {
        val zoneKey = ZoneKey.from(obj.coords)
        val entryList = objs[zoneKey] ?: return false
        val removed = entryList.remove(obj)
        if (!removed) {
            return false
        }
        updates.objDel(obj)
        return true
    }

    public fun reveal(obj: Obj) {
        val zoneKey = ZoneKey.from(obj.coords)
        val entryList = objs.getOrPut(zoneKey)
        if (obj !in entryList) {
            return
        }
        obj.reveal()
        updates.objReveal(obj)
    }

    public fun findAll(zone: ZoneKey): Sequence<Obj> {
        val stacks = objs[zone] ?: return emptySequence()
        return sequence {
            for (entry in stacks.entries) {
                yield(entry)
            }
        }
    }

    public fun findAll(coords: CoordGrid): Sequence<Obj> {
        val stacks = objs[ZoneKey.from(coords)] ?: return emptySequence()
        return stacks.findAll(coords)
    }

    public fun isInvalid(observer: Player, obj: Obj): Boolean = !isValid(observer, obj)

    public fun isValid(observer: Player, obj: Obj): Boolean =
        findAll(obj.coords).any { it.type == obj.type && it.isVisibleTo(observer) }

    private fun Obj.isStackable(): Boolean = objTypes[type]?.isStackable == true

    private fun Obj.canMergeWith(other: Obj, stackable: Boolean): Boolean {
        if (type != other.type) {
            return false
        }
        // Only private objs can merge.
        if (!isPrivate || receiverId != other.nullableReceiverId) {
            return false
        }
        if (!stackable) {
            return false
        }
        val totalCount = count.toLong() + other.count
        return totalCount <= Int.MAX_VALUE
    }

    private fun Obj.singleCopy(): Obj =
        Obj(coords, entity.copy(count = 1), creationCycle, receiverId)

    public companion object {
        /**
         * The maximum [Obj.count] allowed to be spawned per [add] call.
         *
         * _This limit only applies to non-stackable objs determined by the respective obj type._
         *
         * @see [org.rsmod.game.type.obj.UnpackedObjType.isStackable]
         */
        public const val MAX_NON_STACK_COUNT_DROP: Int = 128
    }
}
