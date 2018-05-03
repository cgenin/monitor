export function sortStringForSorter(converter = (a) => a) {
  return (a, b) => {
    const strA = converter(a) || '';
    const strB = converter(b) || '';
    return strA.localeCompare(strB);
  };
}

export function filteringByAttribute(attr) {
  if (!attr) {
    return (l) => l;
  }
  return (original, filter) => {
    if (!filter || filter.length === 0) {
      return original;
    }
    const upFilter = filter.toUpperCase();
    return original.filter(o => {
      const value = o[attr];
      const v = (value || '').toUpperCase();
      return v.indexOf(upFilter) !== -1;
    });
  };
}

export default function filtering(original, filter) {
  if (!filter || filter.trim() === '') {
    return original;
  }

  const upFilter = filter.toUpperCase();
  return original
    .filter(p => {
      const data = JSON.stringify(Object.values(p)).toUpperCase();
      return data.indexOf(upFilter) !== -1;
    });
}

export function sortString(str1, str2) {
  if (str1 < str2) {
    return -1;
  }

  if (str1 > str2) {
    return 1;
  }

  return 0;
}
