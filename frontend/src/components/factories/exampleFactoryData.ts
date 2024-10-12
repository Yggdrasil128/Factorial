export const exampleFactoryData = {
  name: 'Main factory',
  icon: null,
  items: [
    {
      id: 1,
      name: 'Iron Ingot',
      icon: {
        url: '/img/Iron_Ingot.webp'
      },
      balances: {
        production: {
          actual: '42/13',
          desiredCurrentChangelist: '42/13',
          desiredAllChangelists: '42/13'
        },
        consumption: {
          actual: '73',
          desiredCurrentChangelist: '73',
          desiredAllChangelists: '73'
        },
        net: {
          actual: '-31',
          desiredCurrentChangelist: '-31',
          desiredAllChangelists: '-31'
        }
      }
    },
    {
      id: 2,
      name: 'Refined Oil',
      icon: {
        url: '/img/Refined_Oil.webp'
      },
      balances: {
        production: {
          actual: '45',
          desiredCurrentChangelist: '90',
          desiredAllChangelists: '135'
        },
        consumption: {
          actual: '30',
          desiredCurrentChangelist: '30',
          desiredAllChangelists: '30'
        },
        net: {
          actual: '-15',
          desiredCurrentChangelist: '0',
          desiredAllChangelists: '15'
        }
      }
    }
  ],
  productionSteps: [
    {
      recipe: {
        name: 'Iron Ingot',
        icon: {
          url: '/img/Iron_Ingot.webp'
        }
      },
      machineCount: {
        actual: '5',
        desiredCurrentChangelist: '5',
        desiredAllChangelists: '5'
      },
      machine: {
        name: 'Smelter',
        icon: {
          url: '/img/Smelter.webp'
        }
      },
      throughput: {
        input: [
          {
            item: {
              name: 'Iron Ore',
              icon: {
                url: '/img/Iron_Ore.webp'
              }
            },
            quantity: {
              actual: '42',
              desiredCurrentChangelist: '42',
              desiredAllChangelists: '42'
            }
          }
        ],
        output: [
          {
            item: {
              id: 1,
              name: 'Iron Ingot',
              icon: {
                url: '/img/Iron_Ingot.webp'
              }
            },
            quantity: {
              actual: '42',
              desiredCurrentChangelist: '42',
              desiredAllChangelists: '42'
            }
          }
        ]
      },
      modifier: []
    },
    {
      recipe: {
        name: 'Reforming Refine',
        icon: {
          url: '/img/Reforming_Refine.webp'
        }
      },
      machineCount: {
        actual: '1',
        desiredCurrentChangelist: '2',
        desiredAllChangelists: '3'
      },
      machine: {
        name: 'Oil Refinery',
        icon: {
          url: '/img/Oil_Refinery.webp'
        }
      },
      throughput: {
        input: [
          {
            item: {
              name: 'Refined Oil',
              icon: {
                url: '/img/Refined_Oil.webp'
              }
            },
            quantity: {
              actual: '30',
              desiredCurrentChangelist: '60',
              desiredAllChangelists: '90'
            }
          },
          {
            item: {
              name: 'Hydrogen',
              icon: {
                url: '/img/Hydrogen.webp'
              }
            },
            quantity: {
              actual: '15',
              desiredCurrentChangelist: '30',
              desiredAllChangelists: '45'
            }
          },
          {
            item: {
              name: 'Coal',
              icon: {
                url: '/img/Coal.webp'
              }
            },
            quantity: {
              actual: '15',
              desiredCurrentChangelist: '30',
              desiredAllChangelists: '45'
            }
          }
        ],
        output: [
          {
            item: {
              id: 2,
              name: 'Refined Oil',
              icon: {
                url: '/img/Refined_Oil.webp'
              }
            },
            quantity: {
              actual: '45',
              desiredCurrentChangelist: '90',
              desiredAllChangelists: '135'
            }
          }
        ]
      },
      modifier: []
    }
  ]
};
