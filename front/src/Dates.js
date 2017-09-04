const twoDigits = (v) => {
  const value = `${v}`;
  return (value.length === 1) ? `0${value}` : value;
};

export function format(long) {
  const date = new Date(long);
  const cMont = `${date.getMonth() + 1}`;
  const fMonth = twoDigits(cMont);
  return `${date.getFullYear()}/${fMonth}/${twoDigits(date.getDate())}`;
}


export function formatYYYYMMDDHHmm(long) {
  const date = new Date(long);
  return `${format(long)} ${date.getHours()}:${twoDigits(date.getMinutes())}`;
}
