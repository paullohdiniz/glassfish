/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.tests;

@javax.enterprise.inject.Alternative
@javax.annotation.ManagedBean
public class TestManagedBeanExtension extends TestManagedBean {

    public static final String TOSTRING_EXT = "TestManagedBeanExtension";

    @javax.annotation.PostConstruct
    public void init() {
        postConstructCount++;
        postConstructMessage += TOSTRING_EXT + "#PostConstruct";
        System.out.println(postConstructMessage);
    }

    @javax.annotation.PreDestroy
    public void destroy() {
        preDestroyCount++;
        preDestroyMessage += TOSTRING_EXT + "#PreDestroy";
        System.out.println(preDestroyMessage);
    }

    
    @Override
    public String toString() {
        return TOSTRING_EXT;
    }

}
