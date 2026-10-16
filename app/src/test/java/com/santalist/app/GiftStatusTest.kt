package com.santalist.app

import com.santalist.app.data.model.GiftStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GiftStatusTest {

    @Test
    fun testNextCycle() {
        assertEquals(GiftStatus.ORDERED, GiftStatus.IDEA.next())
        assertEquals(GiftStatus.RECEIVED, GiftStatus.ORDERED.next())
        assertEquals(GiftStatus.WRAPPED, GiftStatus.RECEIVED.next())
        assertEquals(GiftStatus.IDEA, GiftStatus.WRAPPED.next())
    }

    @Test
    fun testEnglishTitles() {
        assertEquals("Idea", GiftStatus.IDEA.title)
        assertEquals("Ordered", GiftStatus.ORDERED.title)
        assertEquals("Received", GiftStatus.RECEIVED.title)
        assertEquals("Wrapped", GiftStatus.WRAPPED.title)
    }

    @Test
    fun testStatusFlags() {
        assertFalse(GiftStatus.IDEA.isSpent)
        assertTrue(GiftStatus.ORDERED.isSpent)
        assertTrue(GiftStatus.RECEIVED.isSpent)
        assertTrue(GiftStatus.WRAPPED.isSpent)

        assertFalse(GiftStatus.IDEA.isCompleted)
        assertFalse(GiftStatus.ORDERED.isCompleted)
        assertTrue(GiftStatus.RECEIVED.isCompleted)
        assertTrue(GiftStatus.WRAPPED.isCompleted)
    }
}
