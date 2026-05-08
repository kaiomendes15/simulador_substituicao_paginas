# Simulador de Algoritmos de Substituição de Páginas

## Resumo

Este relatório documenta a implementação de um simulador de algoritmos de substituição de páginas, desenvolvido como trabalho prático da disciplina de Sistemas Operacionais. O objetivo foi colocar em prática os conceitos de gerenciamento de memória, memória virtual e paginação vistos em sala de aula.

---

## Introdução

Quando a memória RAM está cheia e um processo precisa de uma página que não está carregada, o sistema operacional precisa escolher qual página remover para abrir espaço. Essa escolha é feita pelos algoritmos de substituição de páginas, e a qualidade dessa escolha afeta diretamente o desempenho do sistema. Quanto mais vezes o SO escolhe mal, mais faltas de página ocorrem e mais tempo é gasto buscando dados no disco.

Na prática, diferentes algoritmos tomam essa decisão de formas distintas: o FIFO remove a página mais antiga, o LRU remove a que faz mais tempo sem ser usada, o NFU remove a menos acessada e o Aging usa uma aproximação do LRU via registradores de deslocamento. Cada abordagem tem vantagens e limitações dependendo do padrão de acesso do programa.

O trabalho consiste em um simulador que recebe uma cadeia de referências de páginas e um número de frames, executa os quatro algoritmos e mostra quantas faltas de página cada um gerou. Também foi desenvolvida uma interface gráfica com Swing que exibe os resultados em um gráfico de barras para facilitar a comparação.

---

## Metodologia

O simulador foi escrito em Java. A estrutura foi dividida em classes com responsabilidades bem definidas para facilitar a adição de novos algoritmos:

- **AlgoritmoSubstituicao**: interface que todos os algoritmos implementam, com os métodos `getNome()` e `executar(int[] cadeia, int quantFrames)`.
- **Ram**: representa os frames da memória física. Controla quais frames estão livres e armazena as páginas carregadas.
- **TabelaDePaginas**: guarda o estado de cada página — se está na RAM, em qual frame, o bit de referência, o contador do NFU e o registrador do Aging.
- **ResultadoSimulacao**: contabiliza os page faults e registra o histórico de cada acesso.
- **EntradaSimulacao**: lê e converte a cadeia de entrada.

Os algoritmos funcionam assim:

**FIFO**: — mantém uma fila com a ordem de chegada das páginas. Na substituição, remove a que está há mais tempo na memória.

**NFU**: — cada página tem um contador que é incrementado a cada acesso. Na substituição, a página com menor contador é removida.

**Aging**: — usa um registrador por página que é deslocado à direita a cada ciclo, com o bit de referência entrando pelo lado mais significativo. A página com menor registrador é a candidata à substituição.

**LRU**: mantém uma lista ordenada pelo último acesso. Na substituição, remove a página que está há mais tempo sem ser acessada.

A interface gráfica tem um campo para a cadeia de páginas, um spinner para o número de frames e um botão para rodar a simulação. O gráfico é desenhado com a API Graphics2D do Swing e tem uma animação de entrada nas barras.

### Como executar

1. Clone o repositório
2. Compile a partir da pasta `src/`:
   ```
   javac -d out src/algoritmos/*.java src/memoria/*.java src/*.java
   ```
3. Execute:
   ```
   java -cp out SimuladorGUI
   ```
4. Digite a cadeia de páginas separada por espaços, escolha o número de frames e clique em **SIMULAR**.

---

## Resultados e Discussão

Os testes foram feitos com a cadeia `1 2 1 3 2 1 4 1 1 2 5 1 2 3 4 5` (16 acessos) e 3 frames:

| Algoritmo | Faltas de Página | % de Faltas |
|-----------|-----------------|-------------|
| FIFO      | 9               | 56%         |
| NFU       | 8               | 50%         |
| Aging     | 8               | 50%         |
| LRU       | 8               | 50%         |

<img width="886" height="673" alt="image" src="https://github.com/user-attachments/assets/8f3c801d-597b-40dd-b8a9-0fd1727c3617" />


O FIFO foi o pior, com 9 faltas. Isso acontece porque ele não leva em conta se uma página está sendo muito usada. Se ela foi carregada antes das outras, sai primeiro. Nessa cadeia, a página 1 é acessada várias vezes mas acaba sendo removida pelo FIFO por ter chegado mais cedo.

NFU, Aging e LRU empataram com 8 faltas. Os três, de formas diferentes, conseguem identificar quais páginas valem mais a pena manter na memória. O LRU olha quando cada página foi usada pela última vez. O NFU olha quantas vezes ela foi usada no total. O Aging combina os dois: registra o histórico de uso ao longo do tempo com o deslocamento de bits, se aproximando do comportamento do LRU sem precisar de suporte de hardware.

---

## Conclusão

O simulador funcionou como esperado e os resultados bateram com o que é discutido na teoria. Algoritmos que consideram o histórico de uso das páginas saem na frente do FIFO quando a cadeia de acessos tem páginas que se repetem com frequência.

Implementar os algoritmos na prática ajudou a entender os conceitos vistos na teoria e em sala de aula, especialmente no NFU e no Aging, onde o controle do estado de cada página ao longo dos acessos é o que define a qualidade da substituição.

A interface gráfica também facilitou a comparação dos resultados, sendo uma boa alternativa comparada às linhas de log de saída.

---

## Referências

PHOENIXNAP. **What is Paging in OS? How Does Paging Work?**. Disponível em: <https://phoenixnap.com/kb/paging>. Acesso em: maio 2026.

SDPM SIMULATOR. **Page Replacement Algorithm Simulator**. Disponível em: <https://sdpm-simulator.netlify.app/>. Acesso em: maio 2026.

UNIVESP. **Sistemas Operacionais – Paginação**. [S.l.]: YouTube, [2017]. Disponível em: <https://youtu.be/4EaBN98dk40>. Acesso em: maio 2026.

UNIVESP. **Sistemas Operacionais – Algoritmos de Substituição de Páginas**. [S.l.]: YouTube, [2017]. Disponível em: <https://youtu.be/j6RMVMUxYmc>. Acesso em: maio 2026.
