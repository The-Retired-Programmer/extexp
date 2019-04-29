/*
 * Copyright 2019 richard linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 <p>APIs for Extexp Control or Executor factories</p>
 
 <p>Any Extexp package that defines specific controls or executors
 needs to implement a factory for the specific set of executors or controls
 and register the factory as a service provider.</p>
 <p>This pattern is used both internally within the extexp module and also can be used
 by builders of additional NBM plug-in modules providing extended functionality for extexp</p>
 <p>Examples of factory implementations include:</p>
 <pre>@ServiceProvider(service = ExecutorFactory.class)
    public class xxxxFactory implements ExecutorFactory {
 ...</pre>
 <p>or</p>
 <pre>@ServiceProvider(service = ControlFactory.class)
    public class xxxxFactory implements ControlFactory {
 ...</pre>
  
 */
package uk.theretiredprogrammer.extexp.api;
