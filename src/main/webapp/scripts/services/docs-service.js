'use strict';

/**
 * @ngdoc service
 * @name panelApp.docsService
 * @description
 * # docsService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('docsService', function($http) {
    return $http.get('/panel/api/files');
  });
