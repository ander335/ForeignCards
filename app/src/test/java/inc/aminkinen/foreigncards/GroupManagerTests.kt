package inc.aminkinen.foreigncards

import inc.aminkinen.foreigncards.logic.GroupManager
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GroupManagerTests {
    @Test
    fun getUpGroupTest1() {
        val filling = HashMap<Int, Int>()

        assertEquals(1, GroupManager.getUpGroup(0, filling))
        assertEquals(4, GroupManager.getUpGroup(1, filling))
        assertEquals(4, GroupManager.getUpGroup(2, filling))
        assertEquals(20, GroupManager.getUpGroup(4, filling))
        assertEquals(null, GroupManager.getUpGroup(20, filling))
    }

    @Test
    fun getUpGroupTest2() {
        val filling = HashMap<Int, Int>()
        for (i in GroupManager.GreenThreshold until GroupManager.BlueThreshold)
            filling[i] = GroupManager.GreenMaxSize

        assertEquals(null, GroupManager.getUpGroup(1, filling))

        filling[GroupManager.BlueThreshold - 1] = GroupManager.GreenMaxSize - 1
        assertEquals(GroupManager.BlueThreshold - 1, GroupManager.getUpGroup(1, filling))
    }

    @Test
    fun getUpGroupTest3() {
        val filling = HashMap<Int, Int>()
        filling[4] = 10
        filling[5] = 9
        filling[6] = 11

        assertEquals(5, GroupManager.getUpGroup(1, filling))
    }

    @Test
    fun getUpGroupTest4() {
        val filling = HashMap<Int, Int>()
        filling[0] = 13
        filling[1] = 33
        filling[4] = 154
        filling[5] = 113

        assertEquals(20, GroupManager.getUpGroup(4, filling))
    }


    @Test
    fun getDownGroupTest1() {
        val filling = HashMap<Int, Int>()

        assertEquals(null, GroupManager.getDownGroup(0, filling))
        assertEquals(0, GroupManager.getDownGroup(1, filling))
        assertEquals(0, GroupManager.getDownGroup(2, filling))
        assertEquals(2, GroupManager.getDownGroup(4, filling))
        assertEquals(2, GroupManager.getDownGroup(20, filling))
    }

    @Test
    fun getDownGroupTest2() {
        val filling = HashMap<Int, Int>()
        filling[2] = GroupManager.YellowsPreferred
        filling[3] = GroupManager.YellowsPreferred - 1

        assertEquals(3, GroupManager.getDownGroup(4, filling))

        filling[3] = GroupManager.YellowsPreferred
        assertEquals(2, GroupManager.getDownGroup(4, filling))

        filling[2] = GroupManager.YellowsPreferred + 1
        assertEquals(3, GroupManager.getDownGroup(4, filling))
    }

    @Test
    fun getDownGroupTest3() {
        val filling = HashMap<Int, Int>()
        filling[2] = 10
        filling[3] = 9

        assertEquals(3, GroupManager.getDownGroup(4, filling))
    }
}
