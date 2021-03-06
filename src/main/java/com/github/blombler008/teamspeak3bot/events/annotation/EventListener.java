/*
 * MIT License
 *
 * Copyright (c) 2019 blombler008
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.blombler008.teamspeak3bot.events.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
    /**
     * The default priority level is Medium and has an instance level of 3
     * @return {@code @Priority.MEDIUM}
     */
    Priority priority() default Priority.MEDIUM;

    enum Priority {

        LOWEST(5), LOW(4), MEDIUM(3), HIGH(2), HIGHEST(1);
        int priority;

        /**
         *
         * @param i - is the priority level of the enums instance
         */
        Priority(int i) {
            priority = i;
        }

        /**
         * <p>
         * Compares two EventListener and returns a value between 1 and -1
         * indicating which EventListener has bigger priority level.
         * </p>
         * The compareTo function is used to compare two EventListener and
         * is returning a value between 1 and -1 indicating the priority of the EventListener
         * {@code @evL} has a bigger or lower priority than this instance EventListener
         *
         * @param evL - is a EventListener instance which compares to this EventListener instance
         * @return
         *          If the EventListener {@code @evL} has a bigger priority than this instance it returns <code>1</code>
         *          If the EventListener (@code @evL} has a lower priority than this instance it return <code>-1</code>
         *          If the EventListener (@code @evL} has the same priority than this instance it return <code>0</code>
         */
        public int compareTo(EventListener evL) {
            if (evL.priority().getLevel() < priority) {
                return 1;
            } else if (evL.priority().getLevel() > priority) {
                return -1;
            } else {
                return 0;
            }
        }

        /**
         *
         * @return the priority level of this instance
         */
        private int getLevel() {
            return priority;
        }

    }
}
