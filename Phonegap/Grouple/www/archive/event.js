<!DOCTYPE html>
<html ng-app="gemStore">
  <head>
    <link rel="stylesheet" type="text/css" href="bootstrap.min.css" />
    <script type="text/javascript" src="angular.min.js"></script>
    <script type="text/javascript" src="app.js"></script>
    <script type="text/javascript" src="products.js"></script>
  </head>

  <body ng-controller="StoreController as store">
    <!--  Store Header  -->
    <header>
      <h1 class="text-center">Flatlander Crafted Gems</h1>
      <h2 class="text-center">� an Angular store �</h2>
    </header>

    <!--  Products Container  -->
    <div class="list-group">
      <!--  Product Container  -->
      <div class="list-group-item" ng-repeat="product in store.products">
        <h3>{{product.name}} <em class="pull-right">{{product.price | currency}}</em></h3>

        <!-- Image Gallery  -->
        <product-gallery></product-gallery>

        <!-- Product Tabs  -->
        <product-tabs></product-tabs>
      </div>

    </div>
  </body>
</html>


<!DOCTYPE html>
<!--
    Copyright (c) 2012-2014 Adobe Systems Incorporated. All rights reserved.

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->
<html ng-app="groupleMod">
    <head>
        <meta charset="utf-8" />
        <meta name="format-detection" content="telephone=no" />
        <meta name="msapplication-tap-highlight" content="no" />
        <!-- WARNING: for iOS 7, remove the width=device-width and height=device-height attributes. See https://issues.apache.org/jira/browse/CB-4323 -->
        <meta name="viewport" content="user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width, height=device-height, target-densitydpi=device-dpi" />
        <link rel="stylesheet" type="text/css" href="css/index.css" />
        <title>Hello World</title>
    </head>
    <body>
        <div class="app">
            <h1>PhoneGap</h1>
            <div id="deviceready" class="blink">
                <p class="event listening">Connecting to Device</p>
                <p class="event received">Device is Ready</p>
            </div>
        </div>
        <script type="text/javascript" src="cordova.js"></script>
        <script type="text/javascript" src="js/index.js"></script>
        <script type="text/javascript">
            app.initialize();
            document.location.href = "login.html";
        </script>
    </body>
</html>

