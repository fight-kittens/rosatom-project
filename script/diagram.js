gantt.parse({
    data: [
      {
        id: 1, text: "Открытие производства оборудования", start_date: "01-05-2020", duration: 18, open: true
      },
      {
        id: 2, text: "Определение рынка сбыта", start_date: "02-05-2020", duration: 4, parent: 1
      },
      {
        id: 3, text: "Определение маркетинговой стратегии", start_date: "07-05-2020", duration: 5, parent: 1
      }
    ],
    links: [
      {id: 1, source: 1, target: 2, type: "1"},
      {id: 2, source: 2, target: 3, type: "0"}
    ]
  });
});