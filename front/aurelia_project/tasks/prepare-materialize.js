import gulp from 'gulp';
import merge from 'merge-stream';
import path from 'path';


export default function prepareMaterialize() {

  let source = 'node_modules/materialize-css/dist';
  let sourceStyle = path.join(source, 'css');
  let sourceFonts = path.join(source, 'fonts/roboto');

  let taskCssMaterialize = gulp.src(path.join(sourceStyle, 'materialize.min.css'), {base: sourceStyle})
    .pipe(gulp.dest('styles'));

  let taskFontsMaterialize = gulp.src(path.join(sourceFonts, '*'), {base: sourceFonts})
    .pipe(gulp.dest('fonts/roboto'));


  return merge(taskCssMaterialize, taskFontsMaterialize);
}