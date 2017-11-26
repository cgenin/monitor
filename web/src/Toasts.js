import {Toast} from 'quasar';

const timeout = 2500;

export function success(html = `<strong>Mise à jour effectuée avec succés. :)</strong>`) {
  Toast.create['positive']({
    html,
    timeout
  });
}

export function error(err, html = `<strong>Erreur Technique</strong>`) {
  console.log(err);
  Toast.create['negative']({
    html,
    timeout
  });
}
