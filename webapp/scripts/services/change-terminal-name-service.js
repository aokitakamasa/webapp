'use strict';

/**
 * @ngdoc service
 * @name panelApp.changeTerminalNameService
 * @description
 * # changeTerminalNameService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('changeTerminalNameService', function($http) {
    return function(terminal) {
      return $http.put('/api/terminals/' + terminal.id + '/name', {
        'name': terminal.newName
      });
    };
  });
