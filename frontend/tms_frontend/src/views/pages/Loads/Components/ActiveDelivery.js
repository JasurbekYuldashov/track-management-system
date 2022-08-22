import React from "react";
import "./ActivePickup.css";
import { Button } from "reactstrap";
const ActiveDelivery = (props) => {
  return (
    <div className="active-pickup">
      <div onClick={() => props.editDelivery(props.data.id)}>
        <p className="font-weight-bold">{props.data.consignee}</p>
        <p>{props.data.date}</p>
      </div>

      {props.canBeChanged && <Button.Ripple
        color="danger"
        onClick={() => props.deleteDelivery(props.data.id)}
      >
        X
      </Button.Ripple>}
    </div>
  );
};

export default ActiveDelivery;
