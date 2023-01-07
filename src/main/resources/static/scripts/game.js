import {fetchJson} from "./utilities/helpers.js";

const gridContainer = document.querySelector('.grid-container');
const gridSize = 10;
gridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`); // To be able to have dynamic grid size in case we want different size of grid.
let rowLetter = 'A'; // The beginning letter of the row.

const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});
const gamePlayerId = params['gp'];

createGrid();

function createGrid() {
    for (let rowNo = 0; rowNo <= gridSize; rowNo++) {
        if (rowNo === 0) {
            createColumnHeaders();
        } else {
            createRowCells();
        }
    }
}

function createColumnHeaders() {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo !== 0) {
            const gridItemText = document.createTextNode(`${columnNo}`);
            gridItem.appendChild(gridItemText);
        }
        gridContainer.appendChild(gridItem);
    }
}

function createRowCells() {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo === 0) {
            const gridItemText = document.createTextNode(rowLetter);
            gridItem.appendChild(gridItemText);
        } else {
            gridItem.setAttribute('id', `${rowLetter + columnNo}`);
        }
        gridContainer.appendChild(gridItem);
    }
    rowLetter = nextChar(rowLetter);
}

function nextChar(c) {
    return String.fromCharCode(c.charCodeAt(0) + 1);
}

if (gamePlayerId !== null) {
        fetchJson(`/api/game_view/${gamePlayerId}`).then((game) => {
            placeShipsOnGrid(game['ships']);
            showGameInfo(game['gamePlayers']);
        });
}

function placeShipsOnGrid(ships) {
    ships.forEach(ship => {
        ship['locations'].forEach(location => {
            const gridCell = document.querySelector(`#${location}`);
            gridCell.setAttribute('style', 'background-color: rgba(12, 25, 25, 0.8)');
        });
    });
}

function showGameInfo(gamePlayers) {
    const gamePlayerViewer = gamePlayers.find(({id}) => id.toString() === gamePlayerId);
    const viewerUsername = gamePlayerViewer['player']['username'];
    const gamePlayerOpponent = gamePlayers.find(({id}) => id.toString() !== gamePlayerId);
    const opponentUsername = gamePlayerOpponent === undefined ? '"waiting_for_opponent"' : gamePlayerOpponent['player']['username'];

    const gameInfoTextField = document.querySelector('#game-info');
    const gameInfoText = document.createTextNode(`${viewerUsername} (you) vs ${opponentUsername}`);
    gameInfoTextField.appendChild(gameInfoText);
}
