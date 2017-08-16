export function success(toast) {
  console.log('ok');
  toast.show("Mise à jour effectuée avec succés. :)", 4000, 'green');
}

export function error(toast, ex) {
  console.error('error', ex);
  toast.show("Erreur technique !!! :(", 4000, 'red');
}
