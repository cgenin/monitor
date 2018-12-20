function twoDigits(v: any): String {
  const value = `${v}`;
  return (value.length === 1) ? `0${value}` : value;
}

export function format(long: number | Date): string {
  const date = new Date(long);
  const cMont = `${date.getMonth() + 1}`;
  const fMonth = twoDigits(cMont);
  return `${date.getFullYear()}/${fMonth}/${twoDigits(date.getDate())}`;
}

export function formatYYYYMMDDHHmm(long: number | Date): string {
  const date = new Date(long);
  return `${format(long)} ${date.getHours()}:${twoDigits(date.getMinutes())}`;
}

export function mavenToDate(string: string): Date {
  return new Date(
    parseInt(string.substring(0, 4)),
    parseInt(string.substring(4, 6)) - 1,
    parseInt(string.substring(6, 8)),
    parseInt(string.substring(8, 10)),
    parseInt(string.substring(10, 12)),
    parseInt(string.substring(12, 14)),
  );
}
