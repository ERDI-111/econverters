document.addEventListener("DOMContentLoaded", () => {
    const getStartedBtn = document.getElementById("getStartedBtn");
    const signInBtn = document.getElementById("signInBtn");

    if (getStartedBtn) {
        getStartedBtn.addEventListener("click", () => {
            alert("Opening Get Started Registration Form...");
        });
    }

    if (signInBtn) {
        signInBtn.addEventListener("click", () => {
            alert("Opening Sign In Modal...");
        });
    }
});