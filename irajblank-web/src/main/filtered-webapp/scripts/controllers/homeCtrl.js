'use strict';

angular.module('blank-iraj')
.controller('HomeCtrl', function ($scope, raajAuthenticationService, $http, $timeout) {
	function refreshFileList() {
		$http.get('api/home/fileList').success(function(fileList) {
			$scope.fileList = fileList;
		});
	}
	
	$scope.upload = function() {
		raajAuthenticationService.createBypassSecurityToken(function (token) {
			//$('#token').val(token);
			var uploadForm = $('#upload');
			uploadForm.attr('action', 'api/home/upload?X-RAAJ-Bypass-Security-Token='+encodeURIComponent(raajAuthenticationService.login+':'+token));
			uploadForm.submit();
			$timeout(function() {
				refreshFileList();
			}, 300);
		});
	};
	
	$scope.download = function(file) {
		raajAuthenticationService.createBypassSecurityToken(function (token) {
			//$('#token').val(token);
			var downloadForm = $('#download');
			downloadForm.attr('action', 'api/home/download?X-RAAJ-Bypass-Security-Token='+encodeURIComponent(raajAuthenticationService.login+':'+token));
			$('#download>input[name="fileName"]').val(file);
			downloadForm.submit();
		});
	};
	
	$scope.title = 'Accueil';
	refreshFileList();
});