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

export function mavenToDate(string) {
  return new Date(
    string.substring(0, 4),
    string.substring(4, 6)-1,
    string.substring(6, 8),
    string.substring(8, 10),
    string.substring(10, 12),
    string.substring(12, 14)
  );
}
