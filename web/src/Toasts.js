import {Notify} from 'quasar';

export const timeout = 2500;
export const defaultMessageError = `Une erreur technique s'est produite.`;

export function success(message = `Mise à jour effectuée avec succés. :)`) {
  Notify.create({
    type: 'positive',
    message,
    timeout
  });
}

export function error(err, message = defaultMessageError) {
  console.log(err);
  Notify.create({
    type: 'negative',
    message,
    timeout
  });
}
