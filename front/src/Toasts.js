export function success(toast) {
  toast.show('Mise à jour effectuée avec succés. :)', 4000, 'green');
}

export function error(toast, ex) {
  console.error('error', ex);
  toast.show('Erreur technique !!! :(', 4000, 'red');
}
