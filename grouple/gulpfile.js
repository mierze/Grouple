'use strict'
var gulp = require('gulp');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var karma = require('gulp-karma');
var clean = require('gulp-clean');
var jasmineNode = require('gulp-jasmine-node');
var server = require('gulp-develop-server');
var git = require('gulp-git');
var Q = require('q');

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

//testing
//full stack test
gulp.task('fs-test', ['ng-test', 'node-test'], function() {
  console.log('complete.');
});
//front-end tests
gulp.task('ng-test', function() {
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
gulp.task('node-test', ['server'], function () {
  return gulp.src(['./spec/api/*spec.js']).pipe(jasmineNode({
    timeout: 10000
  }));
});
//start server
gulp.task('server', function() {
    server.listen( { path: './index.js' } );
    var deferred = Q.defer();
    // setTimeout could be any async task
    setTimeout(function () {
        deferred.resolve();
    }, 5000);
    return deferred.promise;
});

//deployment
gulp.task('git-init', function() {
  git.init(function (err) {
    if (err) throw err;
  });
});
gulp.task('git-remote', ['git-init'], function() {
  git.addRemote('origin', 'https://git.heroku.com/groupleapp.git', function (err) {
    if (err) throw err;
  });
});
gulp.task('git-add'/*,['git-remote']*/, function() {
  return gulp.src('.')
    .pipe(git.add());
});
gulp.task('git-commit', ['git-add'], function() {
  return gulp.src('.')
    .pipe(git.commit('Update'));
});
gulp.task('git-push', ['git-commit'], function() {
  git.push('origin', 'master', function (err) {
    if (err) throw err;
    this.emit('end'); //instead of erroring the stream, end it
  });
});
gulp.task('deploy', ['git-push'], function(done) {
  done();
});