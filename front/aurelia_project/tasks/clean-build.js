import gulp from 'gulp';
import clean from 'gulp-clean';
import merge from 'merge-stream';

export default function cleanScripts() {

  const jsClean =  gulp.src('scripts/*.js', {read: false})
    .pipe(clean());

  const mapClean =  gulp.src('scripts/*.map', {read: false})
    .pipe(clean());

  return merge(jsClean, mapClean);
}