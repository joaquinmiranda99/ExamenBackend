export interface IUsuario {
  id?: number;
  email?: string;
  firstname?: string;
  lastname?: string;
}

export const defaultValue: Readonly<IUsuario> = {};
