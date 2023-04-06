import {fetchJson, logout, showPlayerUsername, loggedInPlayerUsername} from "./utilities/helpers.js";

const gridSize = 10;
const shipsGridContainer = document.querySelector('#ships-grid-container');
const salvoesGridContainer = document.querySelector('#salvoes-grid-container');
const loggedInPlayerUsernameArea = document.getElementById("logged-in-player");
const logoutBtn = document.querySelector('#logout-btn');
shipsGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`); // To be able to have dynamic grid size in case we want different size of grid.
salvoesGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`);
let rowLetterShip = 'A'; // The beginning letter of the row.
let rowLetterSalvo = 'A';

const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});
const gamePlayerId = params['gp'];

if (loggedInPlayerUsername) {
    showPlayerUsername(loggedInPlayerUsername, loggedInPlayerUsernameArea);
    logoutBtn.setAttribute('style', 'visibility: visible');
    logoutBtn.addEventListener('click', () => logout());
}

createGrids();
placeDataOnGrids();

function createGrids() {
    for (let rowNo = 0; rowNo <= gridSize; rowNo++) {
        if (rowNo === 0) {
            createColumnHeaders(shipsGridContainer);
            createColumnHeaders(salvoesGridContainer);
        } else {
            createRowCells(shipsGridContainer, rowLetterShip);
            createRowCells(salvoesGridContainer, rowLetterSalvo);
        }
    }
}

function createColumnHeaders(gridContainer) {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo !== 0) {
            const gridItemText = document.createTextNode(`${columnNo}`);
            gridItem.appendChild(gridItemText);
        }
        gridContainer === shipsGridContainer ? shipsGridContainer.appendChild(gridItem) : salvoesGridContainer.appendChild(gridItem);
    }
}

function createRowCells(gridContainer, rowLetter) {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('style', 'opacity:0.6');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo === 0) {
            const gridItemText = document.createTextNode(rowLetter);
            gridItem.appendChild(gridItemText);
        } else {
            gridContainer === shipsGridContainer ?
                gridItem.setAttribute('id', `SHIP${rowLetter + columnNo}`) :
                gridItem.setAttribute('id', `SALVO${rowLetter + columnNo}`);
        }
        gridContainer === shipsGridContainer ?
            shipsGridContainer.appendChild(gridItem) :
            salvoesGridContainer.appendChild(gridItem);
    }
    rowLetter === rowLetterShip ?
        rowLetterShip = nextChar(rowLetterShip) :
        rowLetterSalvo = nextChar(rowLetterSalvo);
}


function nextChar(c) {
    return String.fromCharCode(c.charCodeAt(0) + 1);
}

function placeDataOnGrids() {
    if (gamePlayerId !== null) {
        fetchJson(`/api/game_view/${gamePlayerId}`).then((game) => {
            placeShipsOnGrid(game['ships']);
            showGameInfo(game['gamePlayers']);
            placeSalvoesOnGrids(game);
        });
    }
}

function placeShipsOnGrid(ships) {
    ships.forEach(ship => {
        ship['shipLocations'].forEach(location => {
            const gridCell = document.querySelector(`#SHIP${location}`);
            if (gridCell) {
                gridCell.setAttribute('style', 'background-color: darkblue');
            } else {
                alert(`The location ${location} is not exist in the grid table. Check you locations.`);
            }
        });
    });
}

function showGameInfo(gamePlayers) {
    const gamePlayerOwner = gamePlayers.find(({id}) => id.toString() === gamePlayerId);
    const ownerUsername = gamePlayerOwner['player']['username'];
    const gamePlayerOpponent = gamePlayers.find(({id}) => id.toString() !== gamePlayerId);
    const opponentUsername = gamePlayerOpponent === undefined ? '"waiting_for_opponent"' : gamePlayerOpponent['player']['username'];

    const gameInfoTextField = document.querySelector('#game-info');
    const gameInfoText = document.createTextNode(`${ownerUsername} (you) vs ${opponentUsername}`);
    gameInfoTextField.innerHTML = '';
    gameInfoTextField.appendChild(gameInfoText);
}

function placeSalvoesOnGrids(game) {
    const gamePlayerOwner = game['gamePlayers'].find(({id}) => id.toString() === gamePlayerId);
    const ownerPlayerId = gamePlayerOwner['player']['id'];
    placeOwnerSalvoes(game['salvoes'][ownerPlayerId]);

    const gamePlayerOpponent = game['gamePlayers'].find(({id}) => id.toString() !== gamePlayerId);
    if (gamePlayerOpponent !== undefined) {
        const opponentPlayerId = gamePlayerOpponent['player']['id'];
        placeOpponentSalvoes(game['salvoes'][opponentPlayerId], game['ships']);
    }
}

function placeOwnerSalvoes(ownerSalvoes) {
    const entriesOwnerSalvoes = Object.entries(ownerSalvoes);
    entriesOwnerSalvoes.forEach(ownerSalvo => {
        const turnNumber = ownerSalvo[0];
        const salvoLocations = ownerSalvo[1];
        salvoLocations.forEach(location => {
            const gridCellInSalvoGrid = document.querySelector(`#SALVO${location}`);
            gridCellInSalvoGrid.setAttribute('style', 'background-color: darkred ; color: white');
            gridCellInSalvoGrid.innerHTML = turnNumber;
        });
    });
}

function placeOpponentSalvoes(opponentSalvoes, ownerShips) {
    const ownerShipsAllLocations = combineOwnerShipsLocations(ownerShips);

    const entriesOpponentSalvoes = Object.entries(opponentSalvoes);
    entriesOpponentSalvoes.forEach(opponentSalvo => {
        const turnNumber = opponentSalvo[0];
        const salvoLocations = opponentSalvo[1];
        salvoLocations.forEach(location => {
            const gridCellInOwnerShipGrid = document.querySelector(`#SHIP${location}`);
            gridCellInOwnerShipGrid.innerHTML = turnNumber;
            if (ownerShipsAllLocations.includes(location)) {
                gridCellInOwnerShipGrid.setAttribute('style', 'background-color: purple ; color: white');
            } else {
                gridCellInOwnerShipGrid.setAttribute('style', 'background-color: darkred ; color: white');
            }
        });
    });
}

function combineOwnerShipsLocations(ownerShips) {
    return ownerShips.reduce((combinedLocationsArray, {shipLocations}) => {
        combinedLocationsArray.push(...shipLocations);
        return combinedLocationsArray;
    }, []);
}
