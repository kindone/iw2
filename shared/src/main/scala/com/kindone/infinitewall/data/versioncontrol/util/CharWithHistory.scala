package com.kindone.infinitewall.data.versioncontrol.util

/**
 * Created by kindone on 2016. 10. 2..
 */
// char with marker inserted/deleted by branch
class CharWithHistory(val char: Char, val insertedBy: Set[String] = Set(), var deletedBy: Set[String] = Set())
