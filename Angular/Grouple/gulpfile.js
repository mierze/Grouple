var gulp = require('gulp');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var karma = require('gulp-karma');
var clean = require('gulp-clean');

var prodFiles = [
        './app/bundle.js',
        './app/**/*.html',
        './app/**/*.css',
        './app/**/*.png',
        './app/**/*.ico'
];

gulp.task('prod',['clean'], function()
{
  gulp.src(prodFiles)
  .pipe(gulp.dest('./www'));
});

gulp.task('clean', function()
{
  return gulp.src(['www'], {read:false})
  .pipe(clean());
});

gulp.task('test', function() {
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

gulp.task('browserify', function()
{
    return browserify('./app/app.js')
        .bundle()
        .pipe(source('bundle.js'))
        .pipe(gulp.dest('./app'));
})

gulp.task('watch', function()
{
    gulp.watch('app/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/**/*.js', ['browserify', 'test']);
    gulp.watch('app/module/**/**/**/*.js', ['browserify', 'test']);
    gulp.watch(['spec/**/*.js'], ['test']);
})

gulp.task('default', ['watch'])
 