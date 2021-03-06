/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

/**
 * Simple builder interface.
 *
 * @param <T>
 *            The type of objects the builder will build.
 */
public interface Builder<T> {
    T build();
}
