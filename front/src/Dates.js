export function format(long) {
  const date = new Date(long);
  const cMont = `${date.getMonth() + 1}`;
  const fMonth = (cMont.length === 1) ? `0${cMont}` : cMont;
  return `${date.getFullYear()}/${fMonth}/${date.getDate() + 1}`;
}
