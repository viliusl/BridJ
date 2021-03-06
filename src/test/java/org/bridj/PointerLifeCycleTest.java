/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj;

import static org.bridj.Pointer.allocateSizeTs;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.bridj.Pointer.Releaser;
import org.junit.Test;

public class PointerLifeCycleTest {
    @Test
    public void deallocTest() throws InterruptedException {
        List<Pointer<SizeT>> ptrs = new ArrayList<Pointer<SizeT>>();
        final int[] released = new int[1];
        int n = 100, m = 3;
        for (int i = 0; i < n; i++) {
            //Pointer p = pointerToAddress(1).validBytes(10);
            Pointer ptr = allocateSizeTs(m);
            ptr = ptr.withReleaser(new Releaser() {

                public void release(Pointer<?> p) {
                    synchronized (released) {
                        released[0]++;
                    }
                }
            }).withoutValidityInformation();
            for (int j = 0; j < m; j++) {
                ptr.setSizeTAtIndex(j, j);
            }
            ptrs.add(ptr);
        }
        gc();
        assertEquals(0, released[0]);
        for (Pointer<SizeT> ptr : ptrs) {
            for (int j = 0; j < m; j++) {
                assertEquals(j, ptr.getSizeTAtIndex(j));
            }
        }
        ptrs.clear();
        gc();
        assertEquals(n, released[0]);
    }
    
    void gc() throws InterruptedException {
        System.gc();
        Thread.sleep(100);
        System.gc();
        Thread.sleep(100);
    }
}
