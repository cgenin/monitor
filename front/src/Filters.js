
export default function filtering(original, filter){
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
