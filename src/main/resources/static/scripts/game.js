import {combineLocationLists, nextChar, showPlayerUsername} from "./utilities/helpers.js";
import {
    fetchGameViewObject,
    getSalvoes,
    getShips,
    getLoggedInPlayerUsername, sendSalvoes,
    sendShips
} from "./utilities/requestsToApi.js";

import {logout} from "./utilities/authorization.js";
import {allShipTypes} from "./utilities/constants.js"

const gridSize = 10;
const lastLetterInMap = String.fromCharCode(97 + gridSize - 1); // Because charcode return array is also zero indexed, we need to subtract 1.
const shipsGridContainer = document.querySelector('#ships-grid-container');
const salvoesGridContainer = document.querySelector('#salvoes-grid-container');
const loggedInPlayerUsernameArea = document.querySelector('#logged-in-player');
const logoutBtn = document.querySelector('#logout-btn');
const goBackGamesButton = document.querySelector('#go-back-games-page');
const placedShipsContainer = document.querySelector('.placed-ships-container');
const removeShipButton = document.querySelector('#removeShip');
const saveShipButton = document.querySelector('#saveShip');
const fireSalvoButton = document.querySelector('#fireSalvo');
const gameHistoryOwnerTableBody = document.querySelector('#game-history-owner');
const gameHistoryOpponentTableBody = document.querySelector('#game-history-opponent');

shipsGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`); // To be able to have dynamic grid size in case we want different size of grid.
salvoesGridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`);
let rowLetterShip = 'a'; // The beginning letter of the row.
let rowLetterSalvo = 'a';
let shipObjectListPlacedByUser = [];
let selectedSalvoLocations = [];
let highestTurnNumberOwner = 0;
let highestTurnNumberOpponent = 0;
let highestTurnNumberOfOwnerSalvoes = 0;
let highestTurnNumberOfOpponentSalvoes = 0;

const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});
const gamePlayerId = params['gp'];
let fetchedGameView = await fetchGameViewObject(gamePlayerId);
const loggedInPlayerUsername = await getLoggedInPlayerUsername();
const gamePlayerOwner = fetchedGameView['gamePlayers'].find(({id}) => id.toString() === gamePlayerId);
let gamePlayerOpponent = fetchedGameView['gamePlayers'].find(({id}) => id.toString() !== gamePlayerId);
const fetchedShipsOfGamePlayer = await getShips(gamePlayerId);
const allLocationsOfPreviouslySavedOwnerShips = combineLocationLists(fetchedShipsOfGamePlayer, 'shipLocations');
const fetchedSalvoesOfGamePlayer = await getSalvoes(gamePlayerId);
const previouslyFiredSalvoLocations = combineLocationLists(fetchedSalvoesOfGamePlayer, 'salvoLocations');
let lastTurnNumber = fetchedSalvoesOfGamePlayer.length === 0
    ? 0
    : Math.max(...fetchedSalvoesOfGamePlayer.map(salvo => salvo.turnNumber));

goBackGamesButton.addEventListener('click', () => window.location.href = '/web/games.html');

if (loggedInPlayerUsername) {
    showPlayerUsername(loggedInPlayerUsername, loggedInPlayerUsernameArea);
    logoutBtn.setAttribute('style', 'visibility: visible');
    logoutBtn.addEventListener('click', () => logout());
}

createGrids();
showGameInfo();
placeShipDataOnGrids();
placeSalvoesOnGrids(fetchedGameView);
showGameHistory(fetchedGameView);

async function updateGameView() {
    fetchedGameView = await fetchGameViewObject(gamePlayerId);
    gamePlayerOpponent = fetchedGameView['gamePlayers'].find(({id}) => id.toString() !== gamePlayerId);
    showGameInfo();
    placeSalvoesOnGrids(fetchedGameView);
    showGameHistory(fetchedGameView);
}

//Using websocket is more efficient than using interval. It can be implemented in the future.
setInterval(updateGameView, 2000);

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
            if (gridContainer === shipsGridContainer) {
                gridItem.setAttribute('id', `SHIP${rowLetter + columnNo}`);
            } else {
                gridItem.setAttribute('id', `SALVO${rowLetter + columnNo}`);
                gridItem.setAttribute('data-isSelected', 'false');
            }
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

function placeShipDataOnGrids() {
    if (gamePlayerId !== null) {
        placeAlreadySavedShipsOnGrid(fetchedShipsOfGamePlayer);
        createShipsForms(fetchedShipsOfGamePlayer);
    }
}

function placeAlreadySavedShipsOnGrid(ships) {
    ships.forEach(ship => {
        ship['shipLocations'].forEach(location => {
            const gridCell = document.querySelector(`#SHIP${location.toLowerCase()}`);
            gridCell ?
                gridCell.setAttribute('style', 'background-color: darkblue') :
                alert(`The location ${location.toLowerCase()} is not exist in the grid table. Check your locations.`);
        });
    });
}

function showGameInfo() {
    const ownerUsername = gamePlayerOwner['player']['username'];
    const opponentUsername = gamePlayerOpponent === undefined ? '"waiting_for_opponent"' : gamePlayerOpponent['player']['username'];

    const gameInfoTextField = document.querySelector('#game-info span');
    const gameInfoText = document.createTextNode(`${ownerUsername} (you) vs ${opponentUsername}`);
    gameInfoTextField.textContent = '';
    gameInfoTextField.appendChild(gameInfoText);
}

function placeSalvoesOnGrids(game) {
    const ownerPlayerId = gamePlayerOwner['player']['id'];
    placeOwnerSalvoes(game['salvoes'][ownerPlayerId]);

    if (gamePlayerOpponent !== undefined) {
        const opponentPlayerId = gamePlayerOpponent['player']['id'];
        placeOpponentSalvoes(game['salvoes'][opponentPlayerId]);
    }
}

function placeOwnerSalvoes(ownerSalvoes) {
    const newOwnerSalvoes = ownerSalvoes
        .filter(ownerSalvo => ownerSalvo['turnNumber'] > highestTurnNumberOfOwnerSalvoes);

    if (newOwnerSalvoes.length > 0) {
        newOwnerSalvoes.forEach(newOwnerSalvo => {
            newOwnerSalvo['salvoLocations'].forEach(location => {
                const gridCellInSalvoGrid = document.querySelector(`#SALVO${location.toLowerCase()}`);
                gridCellInSalvoGrid.setAttribute('style', 'background-color: darkred ; color: white');
                gridCellInSalvoGrid.textContent = newOwnerSalvo['turnNumber'];
            });
        });
        highestTurnNumberOfOwnerSalvoes = Math.max(...newOwnerSalvoes.map(newOwnerSalvo => newOwnerSalvo['turnNumber']));
    }
}

function placeOpponentSalvoes(opponentSalvoes) {
    const newOpponentSalvoes = opponentSalvoes
        .filter(opponentSalvo => opponentSalvo['turnNumber'] > highestTurnNumberOfOpponentSalvoes);

    if (newOpponentSalvoes.length > 0) {
        newOpponentSalvoes.forEach(newOpponentSalvo => {
            newOpponentSalvo['salvoLocations'].forEach(location => {
                const gridCellInOwnerShipGrid = document.querySelector(`#SHIP${location.toLowerCase()}`);
                gridCellInOwnerShipGrid.textContent = newOpponentSalvo['turnNumber'];
                gridCellInOwnerShipGrid.setAttribute('style', 'background-color: darkred ; color: white');
            });
        });
        highestTurnNumberOfOpponentSalvoes = Math.max(...newOpponentSalvoes.map(newOpponentSalvo => newOpponentSalvo['turnNumber']));
    }
}

function createShipsForms(fetchedShipsOfGamePlayer) {
    const shipsToPlaceContainer = document.querySelector('.ships-to-place-container');

    const shipsAlreadySentInGame = fetchedShipsOfGamePlayer
        .map(({shipType}) => shipType.toLowerCase());

    allShipTypes.forEach(ship => {
        const container = document.createElement('div');
        container.classList.add('form-check');

        const input = createInput(ship, 'radio');
        const label = createLabel(ship, 'radio');

        if (shipsAlreadySentInGame.includes(ship.name.toLowerCase())) {
            input.disabled = true;
            label.textContent += ' (saved)'
        }

        container.appendChild(input);
        container.appendChild(label);
        shipsToPlaceContainer.appendChild(container);
    });
}

function createInput(selectedShipData, type) {
    const input = document.createElement('input');
    input.classList.add('form-check-input');
    input.setAttribute('type', type);
    input.setAttribute('id', `${selectedShipData.id}_${type}`);
    if (type === 'radio') {
        input.setAttribute('name', 'shipsToPlace');
    }
    return input;
}

function createLabel(selectedShipData, type) {
    const label = document.createElement('label');
    label.classList.add('form-check-label');
    label.setAttribute('for', `${selectedShipData.id}_${type}`);
    label.textContent = `${selectedShipData.name} (${'\u25A0 '.repeat(selectedShipData.size)})`;
    return label;
}

function handleShipGridItemClick(event) {
    const clickedItemId = event.target.id;
    const clickedItemGridCode = clickedItemId.slice(4);
    const selectedShip = getSelectedShip();
    const selectedDirection = getSelectedDirection();
    if (fetchedShipsOfGamePlayer.length === 5) {
        return;
    }
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
            placeSelectedShipOnGrid(shipObject, selectedShipData);
        } else {
            alert('Ship cannot be placed.');
        }
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

function placeSelectedShipOnGrid(shipObject, selectedShipData) {
    const allLocationsOfPlacedShips = combineLocationLists(shipObjectListPlacedByUser, 'shipLocations');
    const combinedSavedAndPlacedShipLocations = [...allLocationsOfPreviouslySavedOwnerShips, ...allLocationsOfPlacedShips];
    const isOverlap = isShipOverlap(shipObject, combinedSavedAndPlacedShipLocations);
    if (isOverlap) {
        alert('Ships are overlapping, select another point.');
    } else {
        shipObject['shipLocations'].forEach(location => {
            const gridCell = document.querySelector(`#SHIP${location.toLowerCase()}`);
            gridCell.setAttribute('style', 'background-color: lightskyblue');
        });
        shipObjectListPlacedByUser.push(shipObject);
        createPlacedShipsCheckBoxes(selectedShipData);
    }
}

function isShipOverlap(shipObject, combinedSavedAndPlacedShipLocations) {
    const selectedShipLocations = shipObject['shipLocations'];
    const allLocationsSet = new Set([...selectedShipLocations, ...combinedSavedAndPlacedShipLocations]);
    return allLocationsSet.size < selectedShipLocations.length + combinedSavedAndPlacedShipLocations.length;
}

function createPlacedShipsCheckBoxes(selectedShipData) {
    const container = document.createElement('div');
    container.classList.add('form-check');
    const input = createInput(selectedShipData, 'checkbox');
    const label = createLabel(selectedShipData, 'checkbox');
    input.addEventListener('click', updateRemoveAndSaveButtons);
    container.appendChild(input);
    container.appendChild(label);
    placedShipsContainer.appendChild(container);

    const placedShipRadioButton = document.querySelector(`#${selectedShipData.id}_radio`);
    placedShipRadioButton.checked = false;
    placedShipRadioButton.disabled = true;
}

function updateRemoveAndSaveButtons() {
    const checkboxes = placedShipsContainer.querySelectorAll('input[type="checkbox"]');
    const isAnyShipChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
    removeShipButton.disabled = !isAnyShipChecked;
    saveShipButton.disabled = !isAnyShipChecked;
}

removeShipButton.addEventListener('click', removeCheckedShips);

function removeCheckedShips() {
    const checkedShipsIds = getCheckedShipsIds();
    checkedShipsIds.forEach(shipId => {
        removeShipFromCheckboxShipList(shipId);
        enableShipOnRadioShipList(shipId);
        removeShip(shipId);
        updateRemoveAndSaveButtons();
    });
}

function getCheckedShipsIds() {
    const checkboxes = placedShipsContainer.querySelectorAll('input[type="checkbox"]');
    return Array.from(checkboxes)
        .filter(checkbox => checkbox.checked)
        .map(checkbox => checkbox.id.split('_')[0]);
}

function removeShipFromCheckboxShipList(shipId) {
    const checkedShip = document.getElementById(shipId + '_checkbox');
    const parentElementOfCheckedShip = checkedShip.closest('.form-check');
    parentElementOfCheckedShip.remove();
}

function enableShipOnRadioShipList(shipId) {
    const shipRadioButton = document.getElementById(shipId + '_radio');
    shipRadioButton.disabled = false;
}

function removeShip(shipId) {
    const shipName = allShipTypes
        .find(ship => ship.id === shipId).name;
    const shipObjectToRemove = shipObjectListPlacedByUser.find(shipObject => shipObject['shipType'] === shipName);
    const locationsShipObjectToRemove = shipObjectToRemove['shipLocations'];
    deleteShipFromMap(locationsShipObjectToRemove);

    const indexToRemove = shipObjectListPlacedByUser.findIndex(shipObject => shipObject['shipType'] === shipName);
    if (indexToRemove !== -1) {
        shipObjectListPlacedByUser.splice(indexToRemove, 1);
    }
}

function deleteShipFromMap(locationsShipObjectToRemove) {
    locationsShipObjectToRemove.forEach(location => {
        const gridCellId = 'SHIP' + location;
        const gridCell = document.getElementById(gridCellId);
        gridCell.setAttribute('style', 'background-color: lightgray');
    });
}

saveShipButton.addEventListener('click', saveCheckedShips);

function saveCheckedShips() {
    const checkedShipsIds = getCheckedShipsIds();
    const checkedShipObjects = checkedShipsIds
        .map(shipId => allShipTypes.find(ship => ship.id === shipId).name)
        .map(shipName => shipObjectListPlacedByUser.find(shipObject => shipObject['shipType'] === shipName));
    const requestBody = {ships: checkedShipObjects};
    sendShips(requestBody, gamePlayerId);
}


function handleSalvoGridItemClick(event) {
    const clickedItemId = event.target.id;
    const clickedItemGridCode = clickedItemId.slice(5);
    const isSelected = event.target.getAttribute('data-isSelected');

    try {
        checkIfPlayerCanFire(clickedItemGridCode, isSelected);
    } catch (error) {
        alert(error.message);
        return;
    }
    if (isSelected === 'false') {
        selectTheSalvoGrid(event, clickedItemGridCode);
    } else {
        deselectTheSalvoGrid(event, clickedItemGridCode)
    }
}

function checkIfPlayerCanFire(clickedItemGridCode, isSelected) {
    if (fetchedGameView['gamePlayers'].length < 2) {
        throw new Error("Wait for your opponent.");
    }
    if (fetchedShipsOfGamePlayer.length < 5) {
        throw new Error("First place all of your ships, then select salvo");
    }
    if(!isTurnOfOwnerPlayer()){
        throw new Error("It is not your turn, wait for your opponent to play.");
    }
    if (previouslyFiredSalvoLocations.includes(clickedItemGridCode)) {
        throw new Error("You fired this point before, pick another point.");
    }
    if (selectedSalvoLocations.length === 5 && isSelected === 'false') {
        throw new Error("You can only select a maximum of 5 cells.");
    }
}

function isTurnOfOwnerPlayer() {
    const ownerPlayerId = gamePlayerOwner['player']['id'];
    const opponentPlayerId = gamePlayerOpponent['player']['id'];
    const ownerSalvoesLength = fetchedGameView['salvoes'][ownerPlayerId].length;
    const opponentSalvoesLength = fetchedGameView['salvoes'][opponentPlayerId].length;

    return gamePlayerOwner['id'] < gamePlayerOpponent['id']  // This means the owner is the creator of the game. And the creator has the right to play first
        ? ownerSalvoesLength <= opponentSalvoesLength
        : ownerSalvoesLength < opponentSalvoesLength;
}

function selectTheSalvoGrid(event, clickedItemGridCode) {
    event.target.style.backgroundColor = 'orange';
    event.target.setAttribute('data-isSelected', 'true');
    selectedSalvoLocations.push(clickedItemGridCode);
    fireSalvoButton.disabled = false;
}

function deselectTheSalvoGrid(event, clickedItemGridCode) {
    event.target.style.backgroundColor = 'lightgray';
    event.target.setAttribute('data-isSelected', 'false');
    let index = selectedSalvoLocations.indexOf(clickedItemGridCode);
    if (index !== -1) {
        selectedSalvoLocations.splice(index, 1);
    }
}

fireSalvoButton.addEventListener('click', fireSalvo);

async function fireSalvo() {
    try {
        const requestBody = createSalvoObject();
        await sendSalvoes(requestBody, gamePlayerId);
        fireSalvoButton.disabled = true;
        selectedSalvoLocations = [];
        lastTurnNumber += 1;
    } catch (error) {
        alert(error.message);
    }
}

function createSalvoObject() {
    return {
        "turnNumber": lastTurnNumber + 1,
        "salvoLocations": selectedSalvoLocations
    };
}

function showGameHistory(fetchedGameView) {
    showHitHistoryOnOwner(fetchedGameView);
    showHitHistoryOnOpponent(fetchedGameView);
}

function showHitHistoryOnOwner(game) {
    const ownerPlayerId = gamePlayerOwner['player']['id'];
    if (game.hasOwnProperty('gameHistory')) {
        const hitsOnOwner = game['gameHistory'][ownerPlayerId];
        const newTurnsOfHitsOnOwner = hitsOnOwner
            .filter(turn => Object.keys(turn)[0] > highestTurnNumberOwner);
        if (newTurnsOfHitsOnOwner.length > 0) {
            updateHistoryTable(newTurnsOfHitsOnOwner, gameHistoryOwnerTableBody);
            highestTurnNumberOwner = Math.max(...newTurnsOfHitsOnOwner.map(turn => Object.keys(turn)[0]));
        }
    }
}

function showHitHistoryOnOpponent(game) {
    if (game.hasOwnProperty('gameHistory') && gamePlayerOpponent) {
        const opponentPlayerId = gamePlayerOpponent['player']['id'];
        const hitsOnOpponent = game['gameHistory'][opponentPlayerId];
        const newTurnsOfHitsOnOpponent = hitsOnOpponent
            .filter(turn => Object.keys(turn)[0] > highestTurnNumberOpponent);
        if (newTurnsOfHitsOnOpponent.length > 0) {
            updateHistoryTable(newTurnsOfHitsOnOpponent, gameHistoryOpponentTableBody);
            highestTurnNumberOpponent = Math.max(...newTurnsOfHitsOnOpponent.map(turn => Object.keys(turn)[0]));
        }
    }
}

function updateHistoryTable(newTurnsOfHitsOnAPlayer, historyTable) {
    newTurnsOfHitsOnAPlayer.sort((firstTurn, secondTurn) => Object.keys(firstTurn)[0] - Object.keys(secondTurn)[0]);
    newTurnsOfHitsOnAPlayer.forEach(turn => {
        const turnNumber = Object.keys(turn)[0];
        const hitInfoOfTheTurn = Object.values(turn)[0];
        const numberOfShipsLeft = hitInfoOfTheTurn['ship_number_left'];
        const shipsHit = hitInfoOfTheTurn['ships_hit'];
        const shipsSunk = hitInfoOfTheTurn['ships_sunk'];
        if (Object.keys(shipsHit).length === 0) {
            addHistoryTableRow(turnNumber, '-', [], numberOfShipsLeft, historyTable, []);
        } else {
            Object.entries(shipsHit).forEach(([shipType, hitLocations]) => {
                addHistoryTableRow(turnNumber, shipType, hitLocations, numberOfShipsLeft, historyTable, shipsSunk);
                turnHitCellsPurple(turnNumber, hitLocations, historyTable);
            });
        }
    });
}

function addHistoryTableRow(turnNumber, shipType, hitLocations, numberOfShipsLeft, historyTable, shipsSunk) {
    const newRow = historyTable.insertRow(0);
    const turnCell = newRow.insertCell(0);
    const shipHitCell = newRow.insertCell(1);
    const numberOfHitsCell = newRow.insertCell(2);
    const shipsLeftCell = newRow.insertCell(3);
    turnCell.textContent = turnNumber;
    shipHitCell.textContent = shipsSunk.includes(shipType) ? `${shipType} (sunk)` : shipType;
    if (hitLocations.length === 0) {
        numberOfHitsCell.innerHTML = '-';
    } else {
        numberOfHitsCell.innerHTML = '&#x1F4A5;'.repeat(hitLocations.length);
    }
    shipsLeftCell.textContent = numberOfShipsLeft;
}

function turnHitCellsPurple(turnNumber, hitLocations, historyTable) {
    let gridCellToTurnPurple;
    hitLocations.forEach(location => {
        if (historyTable === gameHistoryOpponentTableBody) {
            gridCellToTurnPurple = document.querySelector(`#SALVO${location.toLowerCase()}`);
        } else {
            gridCellToTurnPurple = document.querySelector(`#SHIP${location.toLowerCase()}`);
        }
        gridCellToTurnPurple.setAttribute('style', 'background-color: purple ; color: white');
        gridCellToTurnPurple.textContent = turnNumber;
    });
}


