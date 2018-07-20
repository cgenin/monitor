import { date } from 'quasar';

export const testConnectionLoading = state => state.testConnectionLoading;
export const events = state => state.infoSchema
  .map((obj) => {
    const dt = (obj.installedon) ? date.formatDate(new Date(obj.installedon), 'YYYY-MM-DD HH:mm:ss') : '';
    const strState = obj.state.name;
    return { dt, strState, ...obj };
  });
