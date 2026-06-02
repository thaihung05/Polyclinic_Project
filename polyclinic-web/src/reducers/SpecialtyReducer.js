const SpecialtyReducer = (current, action) =>{
    switch(action.type){
        case "SET_LOADING":
            return ({...current, loading: action.payload});
        case "SET_ERROR":
            return ({...current, error:action.payload});
        case "LOAD_SPECIALTIES":
            return ({...current, specialties:action.payload, loading:false, error:''});
        case "LOAD_DOCTORS":
            return ({...current, doctors: action.payload, loading:false, error:''});
        default:
            return current;
    }
}

export default SpecialtyReducer;