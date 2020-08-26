export interface ILoan {
  id?: number;
  total?: number;
  usuarioFirstname?: string;
  usuarioId?: number;
}

export const defaultValue: Readonly<ILoan> = {};
