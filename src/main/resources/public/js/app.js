jQuery(function($) {
    var app = {
        stomp : null,
        client: null,
        bidSessionId : null,
        dealerId: null,

        init: function(bidSessionId, dealerId) {
            this.bidSessionId = bidSessionId;
            this.dealerId = dealerId;
        },

        connect: function() {
            var socket = new SockJS('/bidding');
            var client = Stomp.over(socket);

            this.client = client;

            client.connect({}, function(frame) {
                client.subscribe("/topics/bid/" + app.bidSessionId, function(res) {
                    var message = JSON.parse(res.body);

                    // TODO: Display error to the user when his bid failed to register.
                    if (message.type === "state-update" || message.type === "bid-successful" || message.type === "bid-failed")
                        app.updateState(message.payload);
                    else if (message.type === "session-ended") {
                        app.showStats(message.payload);
                        app.disconnect();
                    }
                });

                app.refreshState();
            });
        },

        disconnect: function() {
            $("#bid").prop("disabled", true);
            app.client.disconnect();
        },

        updateState: function(state) {
            $("#vehicle-vin").text(state.bidSession.vehicle.vin);
            $("#bid-starting-price").text(state.bidSession.initialPrice);
            $("#bid-start-date").text(state.bidSession.start);
            $("#bid-end-date").text(state.bidSession.end);

            if (state.highestBid) {
                if (state.highestBid.dealer.id == app.dealerId) {
                    $("#current-user-in-lead").show().find(".highest-bid").text(state.highestBid.price);
                    $("#current-user-not-in-lead").hide();
                } else {
                    $("#current-user-in-lead").hide();
                    var $othersInTheLead = $("#current-user-not-in-lead");

                    $othersInTheLead.find("#leading-dealer").text(state.highestBid.dealer.id);
                    $othersInTheLead.find(".highest-bid").text(state.highestBid.price);

                    $othersInTheLead.show();
                }
            }

            var $dealersList = $("#current-bidders").empty();
            if (state.dealers) {
                state.dealers.forEach((item) => {
                    $dealersList.append($("<li>").text("Dealer#" + item.id));
                })
            }
        },

        bid: function() {
            this.getClient().send("/bidding/bid/" + this.bidSessionId + "/" + this.dealerId, {})
        },

        getClient : function() {
            return this.client;
        },

        refreshState : function() {
            this.getClient().send("/bidding/bid/" + this.bidSessionId)
        },

        showStats : function(stats) {
            var $bidResults = $("#bid-results");

            if (stats.winningBid) {
                $(".winning-price", $bidResults).text(stats.winningBid.price);

                if (stats.winningBid.dealer.id == app.dealerId) {
                    $("#user-winner", $bidResults).show();
                    $("#user-not-winner", $bidResults).hide();
                } else {
                    $("#user-winner", $bidResults).hide();
                    $("#user-not-winner", $bidResults).show().find("#winning-dealer").text("Dealer#" + stats.winningBid.dealer.id);
                }
            }

            $("#total-dealer-participated", $bidResults).text(stats.dealerWithBidsCount);
            $("#total-bids", $bidResults).text(stats.acceptedBidsCount);
            $("#bids-per-second", $bidResults).text(stats.bidsProcessedPerSecond);

            $bidResults.show();
        }
    };


    $("#connect-wss").on("click", function() {
        app.init($("#bidSessionId").val(), $("#dealerId").val());
        app.connect();

        $(this).prop("disabled", true);
        $("#bid").prop("disabled", false);
        return false;
    });


    $("#bid").on("click", function() {
        app.bid();
        return false;
    });
});
