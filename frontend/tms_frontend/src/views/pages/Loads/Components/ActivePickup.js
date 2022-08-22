import React from "react";
import "./ActivePickup.css";
import { Button } from "reactstrap";
import { Paperclip } from 'react-feather';
const ActivePickup = (props) => {
  return (
    <div className="active-pickup">
      <div onClick={() => props.editPickup(props.data.id)}>
        <p className="font-weight-bold">{props.data.shipper}</p>
        <p>{props.data.date}</p>

      </div>

      <div className="d-flex align-items-center">
      { props.canBeChanged && props.has_attachment && <Paperclip color="darkgray"/>}
        {props.canBeChanged && <Button.Ripple
      className="ml-1"
        color="danger"
        onClick={() => props.deletePickup(props.data.id)}
      >
        X
      </Button.Ripple>}
      </div>

    </div>
  );
};

export default ActivePickup;
