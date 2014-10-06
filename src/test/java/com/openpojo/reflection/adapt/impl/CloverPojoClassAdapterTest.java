/*
 * Copyright (c) 2010-2014 Osman Shoukry
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.openpojo.reflection.adapt.impl;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.adapt.impl.sampleclasses.CloverInstrumentedClass;
import com.openpojo.reflection.coverage.impl.Clover3;
import com.openpojo.reflection.coverage.impl.Clover4;
import com.openpojo.reflection.coverage.service.PojoCoverageFilterService;
import com.openpojo.reflection.coverage.service.PojoCoverageFilterServiceFactory;
import com.openpojo.reflection.coverage.service.impl.DefaultPojoCoverageFilterService;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.registry.ServiceRegistrar;
import com.openpojo.validation.affirm.Affirm;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author oshoukry
 */
public class CloverPojoClassAdapterTest {


    private PojoClass cloverInstrumentedPojoClass;
    private PojoClass cloverCleanedPojoClass;
    private static PojoCoverageFilterService originalPojoCoverageFilterService;
    private static PojoCoverageFilterService emptyPojoCoverageFilterService = new DefaultPojoCoverageFilterService();
    private static PojoCoverageFilterService cloverPojoCoverageFilterService = PojoCoverageFilterServiceFactory
            .createPojoCoverageFilterServiceWith(Clover3.getInstance());

    @BeforeClass
    public static void initialSetup() {
        originalPojoCoverageFilterService = ServiceRegistrar.getInstance().getPojoCoverageFilterService();
    }

    @Before
    public void setup() {
        ServiceRegistrar.getInstance().setPojoCoverageFilterService(emptyPojoCoverageFilterService);
        cloverInstrumentedPojoClass = PojoClassFactory.getPojoClass(CloverInstrumentedClass.class);
        ServiceRegistrar.getInstance().setPojoCoverageFilterService(cloverPojoCoverageFilterService);
        cloverCleanedPojoClass = CloverPojoClassAdapter.getInstance().adapt(cloverInstrumentedPojoClass);
    }

    @AfterClass
    public static void reInstateInitialSetup() {
        ServiceRegistrar.getInstance().setPojoCoverageFilterService(originalPojoCoverageFilterService);
    }

    @Test
    public void ensureCloverInstrumentedClassNotChanged() {
        int expectedFields = 4;
        if (Clover4.getInstance().isLoaded())
            expectedFields++;
        Affirm.affirmEquals("Fields added/removed?", expectedFields, cloverInstrumentedPojoClass.getPojoFields().size());
        Affirm.affirmEquals("Methods added/removed?", 3, cloverInstrumentedPojoClass.getPojoMethods().size());
    }

    @Test
    public void shouldSkipFieldsStartingWith__CLR() {
        Affirm.affirmEquals("Cobertura fields not filtered?", 2, cloverCleanedPojoClass.getPojoFields().size());
    }

    @Test
    public void shouldNotSkipAnyMethods() {
        Affirm.affirmEquals("Cobertura methods not filtered?", 3, cloverCleanedPojoClass.getPojoMethods().size());
    }

}
