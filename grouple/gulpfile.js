'use strict'
var gulp = require('gulp');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var karma = require('gulp-karma');
var clean = require('gulp-clean');
var jasmineNode = require('gulp-jasmine-node');
var server = require('gulp-develop-server');

//defaults
gulp.task('default', ['watch']);
gulp.task('watch', function() {
  gulp.watch('app/**/*', ['build', 'test']);
  gulp.watch(['spec/**/*.js'], ['test']);
});

//building
//create production public folder
gulp.task('build', ['browserify', 'clean'], function() {
  //files to move over
  var buildFiles = [
    './app/bundle.js',
    './app/**/*.html',
    './app/**/*.css',
    './app/**/*.png',
    './app/**/*.ico'
  ];
  gulp.src(buildFiles)
  .pipe(gulp.dest('./www'));
});
gulp.task('browserify', function() {
  return browserify('./app/app.js')
    .bundle()
    .pipe(source('bundle.js'))
    .pipe(gulp.dest('./app'));
});
//clean out public directory
gulp.task('clean', function() {
  return gulp.src(['www'], {read:false})
  .pipe(clean());
});

//testing chain
//start server
gulp.task('server', function() {
    server.listen( { path: './index.js' } );
});
//front-end tests
gulp.task('test', ['server'], function() {
  return gulp.src('./foobar')
    .pipe(karma({
      configFile: 'spec/karma.conf.js',
      action: 'run'
    }))
    .on('error', function(err) {
      // Make sure failed tests cause gulp to exit non-zero
      console.log(err);
      this.emit('end'); //instead of erroring the stream, end it
    });
});
//back-end tests
gulp.task('node-test', ['test'], function () {
  return gulp.src(['./spec/api/*spec.js']).pipe(jasmineNode({
    timeout: 10000
  }));
});