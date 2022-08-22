import React from "react";
import { Card, CardBody } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { ChevronDown } from "react-feather";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import { Link } from "react-router-dom";
import * as Icon from "react-feather";
class List extends React.Component {
  state = {
    rowData: [
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
      {
        load: "Essie",
        trip: "Vaill",
        status: "Litronic Industries",
        pickup: "14225 Hancock Dr",
        delivery: "Anchorage",
        driver: "Anchorage",
        truck: "AK",
        customer: "99515",
        from: "907-345-0962",
        to: "907-345-1215",
        bol: "essie@vaill.com",
        total: "http://www.essievaill.com",
        actions: 3574,
      },
    ],
    paginationPageSize: 20,
    defaultColDef: {
      sortable: true,
      editable: false,
      resizable: true,
      suppressMenu: true,
      tooltip: (params) => {
        return params.value;
      },
    },
    columnDefs: [
      {
        headerName: "Load #",
        field: "load",
        width: 150,
        filter: true,
        checkboxSelection: true,
      },
      {
        headerName: "Trip #",
        field: "trip",
        filter: true,
        width: 100,
      },
      {
        headerName: "Status",
        field: "status",
        filter: true,
        width: 150,
      },
      {
        headerName: "Pickup",
        field: "pickup",
        filter: true,
        width: 100,
      },
      {
        headerName: "Delivery",
        field: "delivery",
        filter: true,
        width: 100,
      },
      {
        headerName: "Driver",
        field: "driver",
        filter: true,
        width: 130,
      },
      {
        headerName: "Truck",
        field: "truck",
        filter: true,
        width: 130,
      },
      {
        headerName: "Customer",
        field: "customer",
        filter: "agNumberColumnFilter",
        width: 130,
      },
      {
        headerName: "From",
        field: "from",
        filter: "agNumberColumnFilter",
        width: 130,
      },
      {
        headerName: "To",
        field: "to",
        filter: "agNumberColumnFilter",
        width: 130,
      },
      {
        headerName: "Total",
        field: "total",
        filter: "agNumberColumnFilter",
        width: 130,
      },
      {
        width: 110,
        headerName: "Actions",
        field: "actions",
        sortable: false,
        editable: false,
        suppressMenu: false,
        cellRendererFramework: function (params) {
          return (
            <Button.Ripple color="success" className="mr-1">
              Edit
            </Button.Ripple>
          );
        },
      },
    ],
  };

  render() {
    const { rowData, columnDefs, defaultColDef } = this.state;
    return (
      <Card className="overflow-hidden agGrid-card">
        <div className="d-flex justify-content-between mb-0">
          <h3>Select loads</h3>
          <div></div>
        </div>
        <CardBody className="py-0 no-pagination">
          {this.state.rowData === <h4>No data</h4> ? null : (
            <div className="ag-theme-material w-100 ag-grid-table">
              <AgGridReact
                enableCellTextSelection="true"
                rowSelection="multiple"
                defaultColDef={defaultColDef}
                columnDefs={columnDefs}
                rowData={rowData}
                colResizeDefault={"shift"}
                animateRows={true}
                floatingFilter={true}
                pagination={false}
                pivotPanelShow="always"
              />
            </div>
          )}
        </CardBody>
      </Card>
    );
  }
}

export default List;
