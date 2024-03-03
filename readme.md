# Trabalho Prático I - AEDS III

#### [Thomas Neuenschwander](https://www.linkedin.com/in/thomas-neuenschwander-87a568267/)
#### [Vitor Manoel Silva](https://www.linkedin.com/in/vitor-silva-41b794236/)

#### [Dataset link](https://www.kaggle.com/datasets/thedevastator/fast-food-restaurants-in-the-united-states)

> This dataset focuses on cataloging over 10,000 fast food restaurants across the United States, as revealed by a study from Datafiniti. It underscores the convenience and prevalence of quick meal options for consumers and also highlights the ample opportunities for individuals looking to open their own fast food business. This dataset is valuable for those interested in understanding the distribution of fast food options in America, whether for starting a business or simply exploring dining choices.

## `Fast_Food_Restaurants.csv`

| Campo       | Tipo     | Descrição                                                           |
| ----------- | -------- | ------------------------------------------------------------------- |
| id          | int      | Identificador único do restaurante.                                 |
| name        | String   | Nome do restaurante, tamanho variável.                              |
| categories  | String[] | Lista de categorias às quais o restaurante pertence.                |
| postalCode  | String   | Código postal do endereço do restaurante, tamanho fixo.             |
| city        | String   | Cidade onde o restaurante está localizado, tamanho variável.        |
| address     | String   | Endereço completo do restaurante, tamanho variável.                 |
| latitude    | Double   | Latitude do restaurante, ponto flutuante para precisão geográfica.  |
| longitude   | Double   | Longitude do restaurante, ponto flutuante para precisão geográfica. |
| dateUpdated | Instant  | Data e hora da última atualização do registro do restaurante.       |
| websites     | String[]   | Lista de sites do restaurante.                              |

## `Restaurant Model`

```java
public record Restaurant(
        int id,
        String name,
        String[] categories,
        String postalCode,
        String city,
        String address,
        Double latitude,
        Double longitude,
        Instant dateUpdated,
        String[] website) {

}
```

## `Fast_Food_Restaurants.bin`

| Campo       | Tipo de Dados no Arquivo | Bytes Reservados                                  | Observação                                                                                     |
| ----------- | ------------------------ | ------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| size          | short  | 2 | Total de bytes gasto no registro
| id          | int                      | 4                                                 | Inteiros são escritos como 4 bytes.                                                            |
| name        | UTF       | 2 (comprimento) + N (dados da string)                       | Strings UTF têm 2 bytes para o comprimento, mais o número de bytes dos dados.                  |
| categories  | UTF Array                | 2 (número de categorias) + Sum(2+N) por categoria | Cada string de categoria tem um overhead de 2 bytes para o comprimento, mais N bytes de dados. |
| postalCode  | UTF                      | 5                       |    Campo de tamanho fixo. postalCode possuem 5 digitos.                                                                                            |
| city        | UTF                      | 2 (comprimento) + N (dados da string) (dados)                       |                                                                                                |
| address     | UTF                      | 2 (comprimento) + N (dados da string) (dados)                       |                                                                                                |
| latitude    | double                   | 8                                                 | Doubles são escritos como 8 bytes.                                                             |
| longitude   | double                   | 8                                                 |                                                                                                |
| dateUpdated | long                     | 8                                                 | A data é escrita em milisegundos desde 1970 (Unix). Longs são escritos como 8 bytes.                                                               |
| website     | UTF                      | 2 (número de sites) + Sum(2+N) por site (dados)                       |                                                                                                |
