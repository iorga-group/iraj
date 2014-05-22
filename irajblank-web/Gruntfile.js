'use strict';

var path = require('path');

module.exports = function (grunt) {
	require('jit-grunt')(grunt, {
		bower: 'grunt-bower-task'
	});
	require('time-grunt')(grunt);

	grunt.initConfig({
		watch: {
			compass: {
				files: ['src/main/filtered-webapp/scss/{,*/}*.{scss,sass}'],
				tasks: ['compass:server']
			},
			styles: {
				files: ['src/main/webapp/styles/{,*/}*.css'],
				tasks: ['autoprefixer']
			},
			js: {
				files: ['src/main/filtered-webapp/scripts/**/{,*/}*.js'],
				tasks: ['jshint:all','copy:js']
			},
			htmltemplates: {
				files: ['src/main/filtered-webapp/**/*.html'],
				tasks: ['htmlhint:partial','copy:templates']
			},
			htmlindex: {
				files: ['src/main/filtered-webapp/index.html'],
				tasks: ['htmlhint:full','copy:index','wiredep','injector']
			}
		},
		// Adds vendor prefixes in CSS when needed
		autoprefixer: {
			options: ['last 1 version'],
			dist: {
				files: [{
					expand: true,
					src: 'src/main/webapp/styles/{,*/}*.css',
				}]
			}
		},
		// Install Bower dependencies
		bower: {
			install: {
				options: {
					bowerOptions: {
						forceLatest: true
					}
				}
			}
		},
		// Injects Bower dependencies directly in index.html
		wiredep: {
			target: {
				src: [
					'src/main/webapp/index.html'
				]
			}
		},
		// Empties folders to start fresh
		clean: {
			webapp: [
				'src/main/webapp/lib',
				'src/main/webapp/scripts',
				'src/main/webapp/templates'
			],
			dist: {
				files: [{
					dot: true,
					src: [
						'.tmp'
					]
				}]
			},
			server: '.tmp'
		},
		// Uses Compass
		compass: {
			options: {
				sassDir: 'src/main/filtered-webapp/scss',
				cssDir: 'src/main/webapp/styles',
				generatedImagesDir: 'src/main/webapp/images/generated',
				imagesDir: 'src/main/filtered-webapp/images',
				javascriptsDir: 'src/main/filtered-webapp/scripts',
				fontsDir: 'src/main/filtered-webapp/fonts',
				importPath: 'src/main/webapp/lib',
				httpImagesPath: '/images',
				httpGeneratedImagesPath: '/images/generated',
				httpFontsPath: '/fonts',
				relativeAssets: false
			},
			dist: {},
			server: {
				options: {
					debugInfo: true
				}
			}
		},
		// Runs multiple tasks asynchronously
		concurrent: {
			server: {
				tasks: ['copy:index','copy:js','copy:templates','compass:server'],
				options: {
	                logConcurrentOutput: true
	            }
			},
			dist: {
				tasks: ['copy:index','copy:js','copy:templates','compass:dist'],
				options: {
	                logConcurrentOutput: true
	            }
			}
		},
		copy: {
			index: {
				expand: true,
				cwd: 'src/main/filtered-webapp',
				dest: 'src/main/webapp',
				src: 'index.html'
			},
			templates: {
				expand: true,
				cwd: 'src/main/filtered-webapp/templates/',
				dest: 'src/main/webapp/templates/',
				src: ['**']
			},
			js: {
				expand: true,
				cwd: 'src/main/filtered-webapp/scripts/',
				dest: 'src/main/webapp/scripts/',
				src: ['**']
			}
		},
		// Uses HTMLHint code quality tool
		htmlhint: {
		    partial: {
		        options: {
		            // Force tags to have a closing pair
		            'tag-pair': true,
		            // Force tags to be lowercase
		            'tagname-lowercase': true,
		            // Force attribute names to be lowercase
		            'attr-lowercase': true,
		            // Force attributes to have double quotes rather than single
		            'attr-value-double-quotes': true,
		            // Force special characters to be escaped
		            'spec-char-escape': true,
		            // Prevent using the same ID multiple times in a document
		            'id-unique': true,
		            // Prevent script tags being loaded in the head for performance reasons
		            'head-script-disabled': true,
		            // Prevent style tags. CSS should be loaded through includes
		            'style-disabled': true
		        },
		        src: ['src/main/webapp/templates/**/*.html']
		    },
		    full: {
		        options: {
		            // Force tags to have a closing pair
		            'tag-pair': true,
		            // Force tags to be lowercase
		            'tagname-lowercase': true,
		            // Force attribute names to be lowercase
		            'attr-lowercase': true,
		            // Force attributes to have double quotes rather than single
		            'attr-value-double-quotes': true,
		            // Force the DOCTYPE declaration to come first in the document
		            'doctype-first': true,
		            // Force special characters to be escaped
		            'spec-char-escape': true,
		            // Prevent using the same ID multiple times in a document
		            'id-unique': true,
		            // Prevent script tags being loaded in the head for performance reasons
		            'head-script-disabled': true,
		            // Prevent style tags. CSS should be loaded through includes
		            'style-disabled': true
		        },
		        src: ['src/main/webapp/index.html']
		    }
		},
		// Injects AngularJS files created in the scripts/controllers folder directly in index.html
		injector: {
			options: {
				transform : function(filepath) {
					var e = path.extname(filepath).slice(1);
					filepath = filepath.substring('/src/main/webapp/'.length); // remove base webapp path
					if (e === 'css') {
						return '<link rel="stylesheet" href="' + filepath + '">';
					} else if (e === 'js') {
						return '<script charset="UTF-8" src="' + filepath + '"></script>';
					} else if (e === 'html') {
						return '<link rel="import" href="' + filepath + '">';
					}
				}
			},
			local_dependencies: {
				files: {
					'src/main/webapp/index.html': ['src/main/webapp/styles/**/*.css', 'src/main/webapp/scripts/**/*.js']
				}
			}
		},
		// Uses JSHint code quality tool
		jshint: {
			options: {
				jshintrc: '.jshintrc'
			},
			all: [
				'src/main/filtered-webapp/scripts/**/{,*/}*.js'
			]
		},
	    // Allow the use of non-minsafe AngularJS files. Automatically makes it
	    // minsafe compatible so Uglify does not destroy the ng references
	    ngmin: {
	    	dist: {
		        files: [{
		        	expand: true,
		        	src: 'src/main/webapp/scripts/**/*.js'
		        }]
	    	}
	    },
	    // Reads HTML for usemin blocks to enable smart builds that automatically
	    // concat, minify and revision files. Creates configurations in memory so
	    // additional tasks can operate on them
		useminPrepare: {
			html: 'src/main/webapp/index.html',
			options: {
				dest: 'src/main/webapp'
			}
		},
		// Performs rewrites based on the useminPrepare configuration
		usemin: {
			html: ['src/main/webapp/{,*/}*.html'],
			css: ['src/main/webapp/styles/{,*/}*.css'],
			options: {
				assetsDirs: ['src/main/webapp']
			}
		},
	    // The following *-min tasks produce minified files in the dist folder
	    cssmin: {
	      options: {
	        root: 'src/main/webapp'
	      }
	    }
	});
	
	grunt.registerTask('serve', [
		'clean:webapp',
		'bower',
		'concurrent:server',
		'wiredep',
		'injector',
		'watch'
	]);

	grunt.registerTask('build', [
		'clean:webapp',
		'bower',
		'concurrent:dist',
		'wiredep',
		'injector',
		'useminPrepare',
		'autoprefixer',
		'ngmin',
		'concat',
		'uglify',
		'cssmin',
		'usemin',
		'clean:dist'
	]);

	grunt.registerTask('default', [
		'build'
	]);
};