'use strict';

/**
 * @ngdoc service
 * @name panelApp.restService
 * @description
 * # restService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('restService', function($http) {
    return $http.get('/panel/api/');
  });
