Android nanovg via NDK samlple
=========
### Ru
Этот проект - тестовое задание и представляет собой пример инициализации и использования [nanovg](https://github.com/memononen/nanovg) через NDK (JNI).
Он базируется на примере из Google Samples, но:

 1. Использует nanovg для вывода треугольника.
 1. Не использует шейдеры.
 1. Представляет вывод OpenGL-графики как компонент.
 
Пример демонстрирует интеграцию OpenGL компонента через GLSurfaceView в обычное Android-приложение, показывая недостатки этого и повествуя о недостатках других способов, обрабатывая жесты способами, предоставленными Android SDK, а также применяя трасформации вручную (а не через вершинный шейдер).
 
### Eng  
It's a program for my job application, so it's tiny. The program - is an example of initialization and using [nanovg](https://github.com/memononen/nanovg) через NDK (JNI).
Generally, it's based on Google Samples app, but:  

 1. It's using nanovg for triangle rendering.
 1. It doesn't use shaders. 
 1. It's tries to show GL graphics as an Android component.

App demonstrates OpenGL integration as a GLSurfaceView-component into a casual Android app. It shows disatvantages of this and tells about disatvantages of other methods. For gesture processing it uses Android SDK way, and applies transformations manually (without using vertex shader).

-----------
<img src="screencast.gif" alt="Drawing" width="350"/>

License
-------
Copyright 2015 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
