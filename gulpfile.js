var gulp = require('gulp');
var gutil = require('gulp-util');
var bower = require('bower');
var concat = require('gulp-concat');
var sass = require('gulp-sass');
var minifyCss = require('gulp-minify-css');
var rename = require('gulp-rename');
var sh = require('shelljs');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var requireGlobify = require('require-globify');

var paths = {
    scss: ['./www/scss/**/*.scss'],
    js: ['./www/js/**/*.js']
};

gulp.task('default', ['browserify', 'sass']);

gulp.task('sass', function (done) {
    gulp.src('./www/scss/app.scss')
        .pipe(sass())
        .on('error', sass.logError)
        .pipe(gulp.dest('./www/builds/'))
        .pipe(minifyCss({
            keepSpecialComments: 0
        }))
        .pipe(rename({extname: '.min.css'}))
        .pipe(gulp.dest('./www/builds/'))
        .on('end', done);
});

gulp.task('browserify', function () {
    // Grabs the app.js file
    //noinspection JSUnresolvedFunction
    return browserify(
        {
            entries: './www/js/app.js',
            debug: true,
            // defining transforms here will avoid crashing your stream
            transform: [requireGlobify]
        })
        .bundle()
        .pipe(source('app.js'))
        // saves it the public/js/ directory
        .pipe(gulp.dest('./www/builds/'));
});

gulp.task('watch', function () {
    gulp.watch(paths.scss, ['sass']);
    gulp.watch(paths.js, ['browserify']);
});

gulp.task('install', ['git-check'], function () {
    return bower.commands.install()
        .on('log', function (data) {
            gutil.log('bower', gutil.colors.cyan(data.id), data.message);
        });
});

gulp.task('git-check', function (done) {
    if (!sh.which('git')) {
        console.log(
            '  ' + gutil.colors.red('Git is not installed.'),
            '\n  Git, the version control system, is required to download Ionic.',
            '\n  Download git here:', gutil.colors.cyan('http://git-scm.com/downloads') + '.',
            '\n  Once git is installed, run \'' + gutil.colors.cyan('gulp install') + '\' again.'
        );
        process.exit(1);
    }
    done();
});
