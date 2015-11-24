var gulp = require('gulp');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var karma = require('gulp-karma');

gulp.task('test', function() {
  // Be sure to return the stream
  // NOTE: Using the fake './foobar' so as to run the files
  // listed in karma.conf.js INSTEAD of what was passed to
  // gulp.src !
  return gulp.src('./foobar')
    .pipe(karma({
      configFile: 'test/karma.conf.js',
      action: 'run'
    }))
    .on('error', function(err) {
      // Make sure failed tests cause gulp to exit non-zero
      console.log(err);
      this.emit('end'); //instead of erroring the stream, end it
    });
});

gulp.task('browserify', function()
{
    //TODO: future change my production bundle in a new area
    //Grabs the app.js file
    return browserify('./app/app.js')
        //bundles it and creates a file called main.js
        .bundle()
        .pipe(source('bundle.js'))
        // saves it the public/js/ directory
        .pipe(gulp.dest('./app'));
})

gulp.task('watch', function() {
    gulp.watch('app/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/**/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/**/**/*.js', ['browserify', 'test']);
    gulp.watch(['test/*.js', 'test/**/*.js'], ['test']);
})

gulp.task('default', ['watch'])
 