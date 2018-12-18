export interface LabelDataTable {
  label: string
  value: string
}

export interface Pagination {
  rowsPerPage: number
}

export const noData = 'Attention aucune donnée disponible.';
export const noDataAfterFiltering = 'Attention aucun résultat. Veuillez affiner votre recherche.';
export const separator = 'horizontal';
export const separatorOptions: LabelDataTable[] = [
  {label: 'Horizontal', value: 'horizontal'},
  {label: 'Vertical', value: 'vertical'},
  {label: 'Cellule', value: 'cell'},
  {label: 'Aucun', value: 'none'},
];
export const pagination:Pagination = {
  rowsPerPage: 30,
};
export const rowsPerPageOptions = [30, 50, 80, 100, 250, 500];

