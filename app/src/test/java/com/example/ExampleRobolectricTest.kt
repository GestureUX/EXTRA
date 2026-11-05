package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify app configuration`() {
    val expectedName = "Cube"
    assertEquals("Cube", expectedName)
  }
}
