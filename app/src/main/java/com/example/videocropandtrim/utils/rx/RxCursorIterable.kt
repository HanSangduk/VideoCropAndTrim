package com.example.videocropandtrim.utils.rx

import android.database.Cursor

class RxCursorIterable(private val mIterableCursor: Cursor) : Iterable<Cursor> {

    override fun iterator(): Iterator<Cursor> {
        return RxCursorIterator.from(mIterableCursor)
    }

    internal class RxCursorIterator(private val mCursor: Cursor) : Iterator<Cursor> {
        override fun hasNext(): Boolean {
            return !mCursor.isClosed && mCursor.moveToNext()
        }

        override fun next(): Cursor {
            return mCursor
        }

        companion object {

            fun from(cursor: Cursor): Iterator<Cursor> {
                return RxCursorIterator(cursor)
            }
        }
    }

    companion object {
        fun from(c: Cursor): RxCursorIterable {
            return RxCursorIterable(c)
        }
    }
}