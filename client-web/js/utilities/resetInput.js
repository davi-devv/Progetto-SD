const resetInput = (input) => {
    // Resets the input to its original value
    input.value = '';

    // Dispatches an event to reset button functionality
    input.dispatchEvent(new Event('input'));
}

export default resetInput;