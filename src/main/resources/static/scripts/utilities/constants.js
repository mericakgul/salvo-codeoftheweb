export const domainUrl = 'http://localhost:8080';
export const allShipTypes = [
    {id: 'carrier', name: 'Carrier', size: 5},
    {id: 'battleship', name: 'Battleship', size: 4},
    {id: 'submarine', name: 'Submarine', size: 3},
    {id: 'destroyer', name: 'Destroyer', size: 3},
    {id: 'patrolboat', name: 'Patrol Boat', size: 2}
];

export const game_history =
    {
        1: [
            {
                1: {
                    "ships_hit": {
                        "destroyer": 2,
                        "submarine": 1
                    },
                    "ship_number_left": 5
                }
            },
            {
                2: {
                    "ships_hit": {
                        "submarine": 3
                    },
                    "ship_number_left": 4
                }
            }
        ],
        2: [
            {
                1: {
                    "ships_hit": {
                        "destroyer": 5
                    },
                    "ship_number_left": 5
                }
            },
            {
                2: {
                    "ships_hit": {
                        "destroyer": 2
                    },
                    "ship_number_left": 5
                }
            }
        ]
    }