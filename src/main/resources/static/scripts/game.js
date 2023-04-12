import {showPlayerUsername, combineShipsLocations, nextChar} from "./utilities/helpers.js";
import {fetchGameViewObject, loggedInPlayerUsername} from "./utilities/requestsToApi.js";

import {logout} from "./utilities/authorization.js";
import {allShipTypes} from "./utilities/constants.js"

const gridSize = 10;
const lastLetterInMap = String.fromCharCode(65 + gridSize - 1); // Because charcode return array is also zero indexed, we need to subtract 1.
const shipsGridContainer = document.querySelector('#ships-grid-container');
const salvoesGridContainer = document.querySelector('#salvoes-grid-container');
const loggedInPlayerUsernameArea = document.querySelector('#logged-in-player');
const logoutBtn = document.querySelector('#logout-btn');
const placedShipsContainer = document.querySelector('.placed-ships-container');

shipsGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`); // To be able to have dynamic grid size in case we want different size of grid.
salvoesGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`);
let rowLetterShip = 'A'; // The beginning letter of the row.
let rowLetterSalvo = 'A';
let shipObjectListPlacedByUser = [];
console.log('shipObjectListPlacedByUser', shipObjectListPlacedByUser);

const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});
const gamePlayerId = params['gp'];
const fetchedGameView = await fetchGameViewObject(gamePlayerId);
const allLocationsOfAlreadySentOwnerShips = combineShipsLocations(fetchedGameView['ships']);
console.log('allLocationsOfAlreadySentOwnerShips', allLocationsOfAlreadySentOwnerShips);

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
            createRowCells(shipsGridContainer, rowLetterShip, handleShipGridItemClick);
            createRowCells(salvoesGridContainer, rowLetterSalvo, handleSalvoGridItemClick);
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

function createRowCells(gridContainer, rowLetter, gridItemCallBack) {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo === 0) {
            const gridItemText = document.createTextNode(rowLetter);
            gridItem.appendChild(gridItemText);
        } else {
            gridContainer === shipsGridContainer ?
                gridItem.setAttribute('id', `SHIP${rowLetter + columnNo}`) :
                gridItem.setAttribute('id', `SALVO${rowLetter + columnNo}`);
            gridItem.addEventListener('click', gridItemCallBack);
        }
        gridContainer === shipsGridContainer ?
            shipsGridContainer.appendChild(gridItem) :
            salvoesGridContainer.appendChild(gridItem);
    }
    rowLetter === rowLetterShip ?
        rowLetterShip = nextChar(rowLetterShip) :
        rowLetterSalvo = nextChar(rowLetterSalvo);
}

function handleShipGridItemClick(event) {
    const clickedItemId = event.target.id;
    const clickedItemGridCode = clickedItemId.slice(4);
    console.log('clickedItemId', clickedItemId);
    console.log('clickedItemGridCode', clickedItemGridCode);
    const selectedShip = getSelectedShip();
    const selectedDirection = getSelectedDirection();
    if (!selectedShip || !selectedDirection) {
        alert('Please select a ship and direction from the menu to place');
    } else {
        const selectedShipData = allShipTypes
            .find(ship => ship.id === selectedShip);
        const shipSize = selectedShipData?.size;
        const shipName = selectedShipData?.name;
        const isThereEnoughSpace = checkIfThereIsEnoughSpace(shipSize, selectedDirection, clickedItemGridCode);
        if (isThereEnoughSpace) {
            const shipObject = createShipObject(shipName, shipSize, selectedDirection, clickedItemGridCode);
            console.log('shipObject', shipObject);
            placeSelectedShipOnGrid(shipObject, selectedShipData);
        } else {
            alert('Ship cannot be placed.');
        }
        console.log('selectedShipData', selectedShipData);
        console.log('selectedShip', selectedShip);
        console.log('shipSize', shipSize);
        console.log('selectedDirection', selectedDirection);
    }
}

function checkIfThereIsEnoughSpace(shipSize, selectedDirection, clickedItemGridCode) {
    if (selectedDirection === 'vertical') {
        const clickedGridLetter = clickedItemGridCode[0];
        const numLettersToEnd = lastLetterInMap.charCodeAt(0) - clickedGridLetter.charCodeAt(0) + 1;
        return numLettersToEnd >= shipSize;
    } else {
        const clickedGridNumber = parseInt(clickedItemGridCode.substring(1), 10);
        const numDigitsToEnd = gridSize - clickedGridNumber + 1;
        return numDigitsToEnd >= shipSize;
    }
}

function createShipObject(shipName, shipSize, selectedDirection, clickedItemGridCode) {
    let shipObject;
    const shipLocationsArray = createShipLocationsArray(clickedItemGridCode, shipSize, selectedDirection);
    shipObject = {
        "shipType": shipName,
        "shipLocations": shipLocationsArray
    };
    return shipObject;
}

function createShipLocationsArray(clickedItemGridCode, shipSize, selectedDirection) {
    const clickedGridNumber = parseInt(clickedItemGridCode.substring(1), 10);
    const clickedGridLetter = clickedItemGridCode[0];

    if (selectedDirection === 'vertical') {
        const startCharCode = clickedGridLetter.charCodeAt(0);
        return Array.from(
            {length: shipSize},
            (_, index) => String.fromCharCode(startCharCode + index) + clickedGridNumber.toString()
        );
    } else {
        return Array.from(
            {length: shipSize},
            (_, index) => clickedGridLetter + (clickedGridNumber + index).toString()
        );
    }
}

function getSelectedShip() {
    const shipsToPlace = document.getElementsByName("shipsToPlace");
    let checkedShipId = null;
    Array.from(shipsToPlace).some((radioButton) => {
        if (radioButton.checked) {
            checkedShipId = radioButton.id;
            return true;
        }
    });
    return checkedShipId?.split('_')[0];
}

function getSelectedDirection() {
    const shipDirections = document.getElementsByName("shipDirection");
    let checkedShipDirection = null;
    Array.from(shipDirections).some(radioButton => {
        if (radioButton.checked) {
            checkedShipDirection = radioButton.id;
        }
    });
    return checkedShipDirection;
}

function placeSelectedShipOnGrid (ship, selectedShipData){
    const allLocationsOfPlacedShips = combineShipsLocations(shipObjectListPlacedByUser);
    console.log('allLocationsOfPlacedShips', allLocationsOfPlacedShips);
    const combinedSavedAndPlacedShipLocations = [...allLocationsOfAlreadySentOwnerShips, ...allLocationsOfPlacedShips];
    console.log('combinedSavedAndPlacedShipLocations', combinedSavedAndPlacedShipLocations);
    const isOverlap = isShipOverlap(ship, combinedSavedAndPlacedShipLocations);
    if(isOverlap) {
        alert('Ships are overlapping, select another point.');
    } else {
        ship['shipLocations'].forEach(location => {
            const gridCell = document.querySelector(`#SHIP${location}`);
            gridCell ?
                gridCell.setAttribute('style', 'background-color: lightblue') :
                alert(`The location ${location} is not exist in the grid table. Check your locations.`);
        });
        shipObjectListPlacedByUser.push(ship);
        createPlacedShipsCheckBoxes(selectedShipData);
    }
}

function isShipOverlap(ship, combinedSavedAndPlacedShipLocations) {
    const selectedShipLocations = ship['shipLocations'];
    const allLocationsSet = new Set([...selectedShipLocations, ...combinedSavedAndPlacedShipLocations]);
    return allLocationsSet.size < selectedShipLocations.length + combinedSavedAndPlacedShipLocations.length;
}

function handleSalvoGridItemClick() {
    console.log('Salvo grid item click handled');
}

function placeDataOnGrids() {
    if (gamePlayerId !== null) {
        placeAlreadySavedShipsOnGrid(fetchedGameView['ships']);
        showGameInfo(fetchedGameView['gamePlayers']);
        placeSalvoesOnGrids(fetchedGameView);
        createShipsForms(fetchedGameView);
    }
}

function placeAlreadySavedShipsOnGrid(ships) {
    ships.forEach(ship => {
        ship['shipLocations'].forEach(location => {
            const gridCell = document.querySelector(`#SHIP${location}`);
            gridCell ?
                gridCell.setAttribute('style', 'background-color: darkblue') :
                alert(`The location ${location} is not exist in the grid table. Check your locations.`);
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
        placeOpponentSalvoes(game['salvoes'][opponentPlayerId]);
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

function placeOpponentSalvoes(opponentSalvoes) {
    const entriesOpponentSalvoes = Object.entries(opponentSalvoes);
    entriesOpponentSalvoes.forEach(opponentSalvo => {
        const turnNumber = opponentSalvo[0];
        const salvoLocations = opponentSalvo[1];
        salvoLocations.forEach(location => {
            const gridCellInOwnerShipGrid = document.querySelector(`#SHIP${location}`);
            gridCellInOwnerShipGrid.innerHTML = turnNumber;
            if (allLocationsOfAlreadySentOwnerShips.includes(location)) {
                gridCellInOwnerShipGrid.setAttribute('style', 'background-color: purple ; color: white');
            } else {
                gridCellInOwnerShipGrid.setAttribute('style', 'background-color: darkred ; color: white');
            }
        });
    });
}

function createShipsForms(game) {
    const shipsToPlaceContainer = document.querySelector('.ships-to-place-container');

    const shipsAlreadySentInGame = game['ships']
        .map(({shipType}) => shipType.toLowerCase());

    allShipTypes.forEach(ship => {
        const container = document.createElement('div');
        container.classList.add('form-check');

        const input = createInput(ship, 'radio');
        const label = createLabel(ship, 'radio');

        if (shipsAlreadySentInGame.includes(ship.name.toLowerCase())) {
            input.disabled = true;
            label.setAttribute('style', 'text-decoration: line-through red');
        }

        container.appendChild(input);
        container.appendChild(label);
        shipsToPlaceContainer.appendChild(container);
    });
}

function createInput(ship, type) {
    const input = document.createElement('input');
    input.classList.add('form-check-input');
    input.setAttribute('type', type);
    input.setAttribute('id', `${ship.id}_${type}`);
    if (type === 'radio') {
        input.setAttribute('name', 'shipsToPlace');
    }
    return input;
}

function createLabel(ship, type) {
    const label = document.createElement('label');
    label.classList.add('form-check-label');
    label.setAttribute('for', `${ship.id}_${type}`);
    label.textContent = `${ship.name} (${'\u25A0 '.repeat(ship.size)})`;
    return label;
}

function createPlacedShipsCheckBoxes(ship) {
    const container = document.createElement('div');
    container.classList.add('form-check');
    const input = createInput(ship, 'checkbox');
    const label = createLabel(ship, 'checkbox');
    const placedShipRadioButton = document.querySelector(`#${ship.id}_radio`);
    placedShipRadioButton.checked = false;
    placedShipRadioButton.disabled = true;

    container.appendChild(input);
    container.appendChild(label);
    placedShipsContainer.appendChild(container);
}
