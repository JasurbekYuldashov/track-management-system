import React from "react";
import {
  // Form,
  Form,
  Button,
  FormGroup,
  Input,
  Label,
  CustomInput,
  Row,
  Col,
  Card,
  CardBody,
  CardTitle,
  CardHeader,
  // Button
} from "reactstrap";
import * as Icon from "react-feather";

const Uploader = (props) => {
  return (
    <Card>
      <CardBody style={{ display: "flex", flexWrap: "wrap", gap: 20 }}>
        {props.prevFiles.map((item) => (
          <div
            style={{
              flex: "30%",
              maxWidth: "30%",
              display: "flex",
              alignItems: "center",
            }}
          >
            <div
              style={{ width: 225 }}
              className="mt-1"
              href={`${window.location.origin}/file/${item.id}`}
              onClick={() =>
                window.open(`${window.location.origin}/file/${item.id}`, "_blank")
              }
            >
              {item.name}
            </div>
            <Button.Ripple
              className="btn-icon mt-1"
              color="red"
              type="button"
              onClick={() => props.deletePrevFile(item.id)}
            >
              <Icon.Trash2 />
            </Button.Ripple>
          </div>
        ))}
        {props.newFiles.map((item) => (
          <div
            style={{
              flex: "30%",
              maxWidth: "30%",
              display: "flex",
              alignItems: "center",
            }}
          >
            <FormGroup>
              <Label for="customFile">Upload file</Label>
              <CustomInput
                type="file"
                onInput={(e) => props.uploadFile(e.target.files[0], item)}
              />
            </FormGroup>
            <Button
              color="danger"
              className="d-flex align-items-center btn-sm ml-2"
              type="button"
              style={{ marginBottom: 4 }}
              onClick={() => props.deleteFile(item)}
            >
              <Icon.X width="18" height="18" />
            </Button>
          </div>
        ))}
        <div style={{ width: "100%" }}>
          <Button
            color="success"
            className="d-flex align-items-center"
            type="button"
            onClick={() => props.newFile()}
          >
            New file
          </Button>
        </div>
      </CardBody>
    </Card>
  );
};

export default Uploader;
